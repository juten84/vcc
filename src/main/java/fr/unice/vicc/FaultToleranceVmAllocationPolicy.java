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
    private Map<Integer, List<Host>> affinityMap;

    public FaultToleranceVmAllocationPolicy(List<? extends Host> list) {
        super(list);
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        int vmID = vm.getId();
        if(vmID%10==0)
        {
            //check all eligible hosts for the class
            for (Host h : affinityMap.get(vmID))
            {

            }
        }
        else
        {

        }
        return false;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
     //Todod

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

        affinityMap.get(vm.getId()/100).add(vm.getHost());
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
