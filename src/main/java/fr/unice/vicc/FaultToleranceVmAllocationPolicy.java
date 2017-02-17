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
    private Map<Integer, List<Host>> TenMultipleMap;

    public FaultToleranceVmAllocationPolicy(List<? extends Host> list) {

        super(list);
        hoster = new HashMap<>();
        TenMultipleMap = new HashMap<Integer, List<Host>>();
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        //TODO
        int vmID = vm.getId();
        if(TenMultipleMap.containsKey(vmID%10) && vmID%10 == 0)
        {
            //check all eligible hosts for the class
            for (Host h : TenMultipleMap.get(vmID % 10))
            {
                if(h.isFailed())
                {
                    for (Host host : getHostList())
                    {
                        if(!host.isFailed())
                        {
                            if (allocateHostForVm(vm, host))
                                return true;
                        }
                    }
                }
                else
                if (allocateHostForVm(vm, h))
                    return true;
            }
        }
        else
        {
            for (Host host : getHostList())
            {
                if (allocateHostForVm(vm, host))
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        if (host.vmCreate(vm)) {
            hoster.put(vm, host);
            return true;
        } else
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
