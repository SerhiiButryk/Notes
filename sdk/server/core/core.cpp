#include "../../common/log.h"
#include "server_manager.h"
#include "impl_services/iml_children_process_operator.h"
#include "impl_services/impl_channel_service.h"
#include "impl_services/impl_exit_service.h"
#include "impl_services/impl_resource_service.h"

using namespace MYLIB;

static const std::string TAG = "MAIN";

int main()
{
    logSystem("server is started");

    ImpChildrenProcessOperator service1;
    ImplExitService service2;
    ImplChannelService service3;
    ImplResourceService service4;

    ServerManager manager(service2, service3, service4, service4, service1);

    manager.initManager();
    manager.createServerChannel();
    manager.onWaitAllProcesses();
    manager.prepareServerExit();
    manager.shutdown();
    
    logSystem("server is fihished");
}