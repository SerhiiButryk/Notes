#include "impl_exit_service.h"

#include <string>
#include <cstring>

#include "../../../common/log.h"

namespace MYLIB 
{
    namespace SERVER
    {
        void ImplExitService::shutdown() {
            std::string answer;

            logSystem("shutdown programm ? (yes/no)");

            while (true) {
                std::cin >> answer;

                if (strcasecmp(answer.c_str(), "yes") == 0) {
                    break;
                }

                logSystem("sure ?");
            }
        }
    }

}