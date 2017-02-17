package fr.unice.vicc;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.*;

/**
 * Created by Justin on 16/02/2017.
 */
public class GreedyVmAllocationPolicy  extends VmAllocationPolicy {

    private Map<Vm, Host> hoster;

    public GreedyVmAllocationPolicy(List<? extends Host> list) {
        super(list);
        setHostList(list);
    }

    @Override
    protected void setHostList(List<? extends Host> hostList) {
        super.setHostList(hostList);
        hoster = new HashMap<>();
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        Collections.sort(getHostList(), (h1, h2) -> (int)(h1.getAvailableMips() - h2.getAvailableMips()));


        for (Host h : getHostList()) {

            boolean possibleHost = false;
            for(Pe p : h.getPeList())
            {
                if(vm.getMips() - 500d < p.getPeProvisioner().getAvailableMips())
                {
                    possibleHost = true;
                    break;
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
