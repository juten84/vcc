package fr.unice.vicc;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Justin on 06/02/2017.
 */
public class FaultToleranceVmAllocationPolicy extends VmAllocationPolicy {

    private Map<Vm, Host> hoster;

    public FaultToleranceVmAllocationPolicy(List<? extends Host> list) {
        super(list);
        hoster = new HashMap<>();
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        int vmId = vm.getId();
        boolean importantVm = false;

        if (vmId % 10 == 0) {
            importantVm = true;
        }

        for (Host host : getHostList()) {
            if (importantVm && host.isSuitableForVm(vm) && host.vmCreate(vm)) {
                hoster.put(vm, host);
                return true;

            }
            if (!importantVm && host.vmCreate(vm)){
                hoster.put(vm, host);
                return true;
            }

        }

        return false;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {

        //Check if it's not one VM we should be carreful with
        if (vm.getId() % 10 != 0 && host.vmCreate(vm)) {
            hoster.put(vm, host);
            return true;
        }
        return false;
    }


    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> list) {
        return null;
    }

    @Override
    public void deallocateHostForVm(Vm vm) {

        Host host = getHost(vm);
        host.vmDestroy(vm);
    }


    @Override
    public Host getHost(Vm vm) {
        Host host = hoster.get(vm);
        return host;
    }

    @Override
    public Host getHost(int vmId, int userId) {
        for (Vm vm : hoster.keySet()) {
            if (vm.getId() == vmId && vm.getUserId() == userId) {
                return getHost(vm);
            }
        }
        return null;
    }
}
