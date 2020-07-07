#ifndef _THREAD_POOL_MLIB_H_
#define _THREAD_POOL_MLIB_H_

#include <thread>
#include <deque>
#include <mutex>
#include <condition_variable>
#include <functional>
#include <atomic>
#include <string>

#include "../common/log.h"

/**
 *  High level serial thread executor implementation
*/
static const std::string TAG = "ExSer";

namespace MYLIB 
{
    using TASK_TYPE = void();

    class Executor
    {
        
        private:
            std::thread _worker;
            std::condition_variable _wake_signal;
            std::mutex _mutex;
            std::atomic<bool> _stop;
            std::deque<std::function<TASK_TYPE>> _queue;

            int _identifier;

            void safeQuit();
            void startThread();
            void run();

            Executor();

        public:
            ~Executor();

            Executor(const Executor&) = delete;
            Executor& operator=(const Executor&) = delete;

            void start();
            void schedule(const std::function<TASK_TYPE>& task, int delay = 0);
            void quit();

            static Executor* getInstance();
    };

    /*
    *   Implementation
    */

    Executor::Executor() : _worker(), _stop(false), _identifier(0) {
        logInfo(TAG,"Executor()");
    }

    Executor::~Executor() {
        logInfo(TAG,"~Executor() IN");
        safeQuit();
        logInfo(TAG,"~Executor() OUT");
    }

    void Executor::start() {
        logInfo(TAG,"Executor::start() IN");

        // Check if thread has already started
        if (_identifier) {
            logInfo(TAG,"thread is already started");
            return;
        }

        _stop = false;
        _identifier = 1;

        startThread();        

        logInfo(TAG,"Executor::start() OUT");
    }

    void Executor::startThread() {
        run();
    }

    void Executor::run() {
        std::thread thread([&](){

            while (!_stop)
            {
                logInfo("TH", "executor running...");

                logInfo("TH","try to equire lock");
                std::unique_lock<std::mutex> lock(_mutex);

                logInfo("TH","waite condition");
                
                // If queue is empty we continue to waite
                _wake_signal.wait(lock, [&]() { return !_queue.empty(); });

                if (_stop) {
                    logInfo("TH","exit worker");    
                    break;
                }

                logInfo("TH","get new task");
                auto run = _queue.back();
                _queue.pop_back();

                logInfo("TH","release lock");
                lock.unlock();

                // process task
                logInfo("TH","execute task");
                run();

                logInfo("TH","task is completed");
            }

        });

        _worker = std::move(thread);
    }

    void Executor::schedule(const std::function<TASK_TYPE>& task, int delay) {
        logInfo(TAG, "Executor::schedule() IN");
        
        std::unique_lock<std::mutex> lock(_mutex);

        _queue.push_back(task);

        lock.unlock();
        _wake_signal.notify_one();

        logInfo(TAG,"Executor::schedule() OUT");
    }

    void Executor::quit() {
        logInfo(TAG," Executor::quit() IN");
        safeQuit();
        logInfo(TAG," Executor::quit() OUT");
    }

    void Executor::safeQuit() {
        _stop = true;

        // Send fake task to stop thread if work is not done yet
        std::function<TASK_TYPE> f([](){});
        schedule(f);                

        if (_worker.joinable()) {
            logInfo(TAG,"wait for worker to finish");
            _worker.join();
        }
    }

    Executor* Executor::getInstance() {
        static Executor executor;
        return &executor;
    }

}

#endif