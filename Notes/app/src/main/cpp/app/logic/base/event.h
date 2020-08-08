#pragma once

#include <map>
#include <string>

namespace APP
{
    /**
     *  Base class for T event types.
     *
     *  Derived classes introduce a new type event
     */
    template <class T>
    class Event
    {
        protected:
            T eventType;
            std::map<std::string, std::string> data;

        public:
            explicit Event(T eventType);
            virtual ~Event() = default;

            T getEventType() const;

            void putData(const std::string& key, const std::string& value);
            std::string getData(const std::string& key) const;
    };

    template <class T>
    Event<T>::Event(T _eventType) : eventType(_eventType)
    {

    }

    template <class T>
    void Event<T>::putData(const std::string& key, const std::string& value)
    {
        data.insert(std::pair(key, value));
    }

    template <class T>
    T Event<T>::getEventType() const
    {
        return eventType;
    }

    template <class T>
    std::string Event<T>::getData(const std::string& key) const
    {
        for (auto& v : data)
        {
            if (v.first == key) {
                return v.second;
            }

        }

        // TODO: throw exception ?
        return std::string();
    }
}