package fr.unice.vicc;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.power.PowerHost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fhermeni2 on 16/11/2015.
 */
public class NaiveVmAllocationPolicy extends VmAllocationPolicy {

    /** The map to track the server that host each running VM. */
    private Map<Vm,Host> hoster;

    public NaiveVmAllocationPolicy(List<? extends Host> list) {
        super(list);
        hoster =new HashMap<>();
    }

    @Override
    protected void setHostList(List<? extends Host> hostList) {
        super.setHostList(hostList);
        hoster = new HashMap<>();
    }

    @Override
    public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> list) {

        return null;
    }

    @Override
    public boolean allocateHostForVm(Vm vm)
    {
        //First fit algorithm, run on the first suitable node
        for (Host h : getHostList()) {
            if (h.vmCreate(vm)) {
                //track the host
                hoster.put(vm, h);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean allocateHostForVm(Vm vm, Host host) {
        return false;
    }

    @Override
    public void deallocateHostForVm(Vm vm) {
        hoster.get(vm).vmDestroy(vm);
    }

    @Override
    public Host getHost(Vm vm) {
        return this.hoster.get(vm);
    }

    @Override
    public Host getHost(int vmId, int userId) {
        return this.hoster.get(Vm.getUid(userId, vmId));
        //?
    }
}
