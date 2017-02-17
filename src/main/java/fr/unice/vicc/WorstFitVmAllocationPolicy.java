package fr.unice.vicc;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

import java.util.*;

/**
 * Created by Justin on 17/02/2017.
 */
public class WorstFitVmAllocationPolicy extends VmAllocationPolicy {

    private Map<Vm, Host> hoster;

    public WorstFitVmAllocationPolicy(List<? extends Host> list) {
        super(list);
        hoster = new HashMap<>();
    }



    @Override
    public boolean allocateHostForVm(Vm vm) {
        List<Host> hosts = getHostList();
        hosts.sort((o1, o2) -> {
            if (o1.getAvailableMips() ==  o2.getAvailableMips()){
                if (o1.getRam() <= o2.getRam()){
                    return 1;
                }else{
                    return -1;
                }
            }else if (o1.getAvailableMips() <=  o2.getAvailableMips()){
                return 1;
            }else{
                return -1;
            }


        });
        Host bestHost = hosts.get(0);
        if(!bestHost.vmCreate(vm)){
            System.err.println("No more place for VM");
            return false;
        }
        hoster.put(vm, bestHost);
        return true;
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
