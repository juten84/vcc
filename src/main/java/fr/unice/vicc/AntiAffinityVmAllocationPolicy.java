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
public class AntiAffinityVmAllocationPolicy extends VmAllocationPolicy {

    private Map<Vm, Host> hoster;
    private Map<Integer, List<Host>> affinityMap;

    public AntiAffinityVmAllocationPolicy(List<? extends Host> list) {
        super(list);
        hoster = new HashMap<>();
        affinityMap = new HashMap<>();
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        int vmID = vm.getId();
        if(affinityMap.containsKey(vmID/100))
        {

            for (Host h : affinityMap.get(vmID/100))
            {
                if (h.vmCreate(vm))
                {
                    affinityMap.get(vmID/100).remove(h);
                    hoster.put(vm, h);
                    return true;
                }
            }
        }
        else
        {
            for (Host h : getHostList())
            {
                if (h.vmCreate(vm))
                {
                    List<Host> possibleHosts = new ArrayList<>();
                    possibleHosts.addAll(getHostList());
                    possibleHosts.remove(h);
                    affinityMap.put(vmID/100, possibleHosts);
                    hoster.put(vm, h);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        int vmClass = vm.getId()/100;
        if(affinityMap.containsKey(vmClass))
        {

            if (affinityMap.get(vmClass).contains(host) && host.vmCreate(vm))
            {
                affinityMap.get(vm.getId()/100).remove(host);
                hoster.put(vm, host);
                return true;
            }
        }
        else
        {
            if (host.vmCreate(vm))
            {
                List<Host> possibleHosts = new ArrayList<>();
                possibleHosts.addAll(getHostList());
                possibleHosts.remove(host);
                affinityMap.put(vmClass, possibleHosts);
                hoster.put(vm, host);
                return true;
            }
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
