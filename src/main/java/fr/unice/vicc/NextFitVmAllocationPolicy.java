package fr.unice.vicc;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Justin on 17/02/2017.
 */
public class NextFitVmAllocationPolicy extends VmAllocationPolicy {

    private Map<Vm, Host> hoster;
    private int i;

    public NextFitVmAllocationPolicy(List<? extends Host> list) {
        super(list);
        hoster = new HashMap<>();
        i = 0;
    }


    @Override
    public boolean allocateHostForVm(Vm vm) {

        while (i < getHostList().size()) {
            if (getHostList().get(i).vmCreate(vm)) {
                hoster.put(vm, getHostList().get(i));
                return true;
            }
            i++;
        }
        return false;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {

        if (host.vmCreate(vm)) {
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
