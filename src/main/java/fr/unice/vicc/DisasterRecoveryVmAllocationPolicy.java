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
public class DisasterRecoveryVmAllocationPolicy  extends VmAllocationPolicy {

    private Map<Vm, Host> hoster;

    private int temp;

    public DisasterRecoveryVmAllocationPolicy(List<? extends Host> list) {
        super(list);
        hoster = new HashMap<>();
        temp = -1;
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {

        for (Host h : getHostList()) {

            if (h.getTotalMips() != temp && h.vmCreate(vm)) {
                    hoster.put(vm, h);
                    temp = h.getTotalMips();
                    return true;
            }
        }
        return false;
    }





    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {

        if (host.getTotalMips() == temp){
            return false;
        }

        if (host.vmCreate(vm)) {
            hoster.put(vm, host);
            temp = host.getTotalMips();
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
