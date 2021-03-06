package fr.unice.vicc;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Justin on 08/02/2017.
 */
public class NoViolationsVmAllocationPolicy extends VmAllocationPolicy {

    private Map<Vm, Host> hoster;

    public NoViolationsVmAllocationPolicy(List<? extends Host> list) {
        super(list);
        hoster = new HashMap<>();
    }


    public boolean allocateHostForVm(Vm vm) {


        for (Host h : getHostList()) {
            boolean possibleHost = false;
            Iterator<Pe> iter = h.getPeList().iterator();
            while (iter.hasNext() && !possibleHost){
                if(vm.getMips() < iter.next().getPeProvisioner().getAvailableMips())
                {
                    possibleHost = true;
                }
            }

            if(possibleHost)
            {
                if (h.vmCreate(vm)) {
                    hoster.put(vm, h);
                    return true;
                }
            }
        }
        return false;
    }






    public boolean allocateHostForVm(Vm vm, Host host) {
        if (host.vmCreate(vm)) {
            hoster.put(vm, host);
            return true;
        }
        return false;
    }

    @Override
    public void deallocateHostForVm(Vm vm) {
        Host host = getHost(vm);
        host.vmDestroy(vm);
    }


    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> arg0) {
        return null;
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
