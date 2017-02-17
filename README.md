# Vicc project: homemade VM Schedulers

This project aims at developing different VM schedulers for a given IaaS cloud. Each scheduler will have meaningful properties for either the cloud customers or the cloud provider.

The implementation and the evaluation will be made over the IaaS cloud simulator [CloudSim](http://www.cloudbus.org/cloudsim/). The simulator will replay a workload extracted from Emulab, on a datacenter having realistic characteristics. 

##The report is at the end of this document.

#### Some usefull resources:

- CloudSim [FAQ](https://code.google.com/p/cloudsim/wiki/FAQ#Policies_and_algorithms)
- CloudSim [API](http://www.cloudbus.org/cloudsim/doc/api/index.html)
- CloudSim [source code](cloudsim-3.0.3-src.tar.gz)
- CloudSim [mailing-list](https://groups.google.com/forum/#!forum/cloudsim)

#### Setting up the environment

You must have a working Java 7 + [maven](http://maven.apache.org) environment to develop and Git to manage the sources. No IDE is required but feel free to use it.

1. clone this repository. The project directory is organized as follow:
```sh
$ tree
 |- src # the source code
 |- repository # external dependencies
 |- planetlab # the workload to process
 |-cloudsim-3.0.3-src.tar.gz # simulator sources
 \- pom.xml # maven project descriptor
```
2. check everything is working by typing `mvn install` in the root directory
3. Integrate the project with your IDE if needed

#### How to test

`fr.unice.vicc.Main` is the entry point. It can be launch from your IDE or using the command `mvn compile exec:java`.

```sh
Usage: Main scheduler [day]
```

- `scheduler` is the identifier of the scheduler to test, prefixed by `--`.
- `day` is optional, it is one of the workload day (see folders in `planetlab`). When `all` is indicated all the days are replayed sequentially.

By default, the output is written in a log file in the `logs` folder.

If you execute the program through `mvn exec:java`, then the arguments are provided using the 'sched' and the 'day' properties.

- To execute the simulator using the `naive` scheduler and all the days:
`mvn compile exec:java -Dsched=naive -Dday=all`
- to replay only day `20110303`: `mvn compile exec:java -Dsched=naive -Dday=20110303`

## Exercices

For this project, you have to develop various VM schedulers.
To integrate your schedulers within the codebase, you will have to declare your schedulers inside the class `VmAllocationPolicyFactory`.

For each implemented scheduler, provide inside the class header:

- the role
- the overal design and technical choices
- the worst-case temporal complexity

### A naive scheduler to start

This first scheduler aims only at discovering the CloudSim API. This scheduler simply places each `Vm` to the first `Host` having enough free resources (CPU and memory).

1. Just create the new class handling the scheduling, integrate it into `VmAllocationPolicyFactory`. Your class must extends `VmAllocationPolicy`. The flag to call this scheduler for the command line interface (CLI) will be "naive". Test if the integration is correct. The code shall crash in your class but that is expected at this stage.
2. Implements the easy part first, that is to indicate where a Vm runs. This is done by the `getHost(Vm)` and the `getHost(int, int)` methods
3. The 2 `allocateHostForVm` are the core of the Vm scheduler. One of the 2 methods will be executed directly by the simulator each time a Vm is submitted. In these methods, you are in charge of compute the most appropriate host for each Vm. Implementing `allocateHostForVm(Vm, Host)` is straighforward as the host is forced. To allocate the Vm on a host look at the method `Host.vmCreate(Vm)`. It allocates and returns true iff the host as sufficient free resources. The method `getHostList` from `VmAllocationPolicy` allows to get the datacenter nodes. Track the way you want the host used to host that Vm.
4. Implements `deallocateHostForVm`, the method that remove a running `Vm` from its hosting node. Find the host that is running your Vm and use `Host.vmDestroy()` to kill it.
5. The scheduler is static. `optimizeAllocation` must returns `null`
6. Now, implement `allocateHostForVm(Vm)` that is the main method of this class. As we said, the scheduler is very simple, it just schedule the `Vm` on the first appropriate `Host`.
7. Test your simulator on a single day. If the simulation terminates successfully, all the VMs have been scheduled, all the cloudlets ran, and the provider revenues is displayed.
8. Test the simulator runs successfully on all the days. For future comparisons, save the daily revenues and the global one. At this stage, it is ok to have penalties due to SLA violations
	
## Support for Highly-Available applications


### Fault-tolerance for replicated applications
Let consider the VMs run replicated applications. To make them fault-tolerant to node failure, the customer expects to have the replicas running on distinct hosts.

1. Implement a new scheduler (`antiAffinity` flag) that places the Vms with regards to their affinity. In practice, all Vms with an id between [0-99] must be on distinct nodes, the same with Vms having an id between [100-199], [200-299], ... .
1. What is the impact of such an algorithm over the cluster hosting capacity ? Why ?

### Preparing for disaster recovery

The previous scheduler ensures fault tolerance to some node failures.
Switches can also fail and in such a circumstance, a lot of nodes become unavailable. Let consider a hierarchical network. The Ml110G4 nodes are connected to one switch. The Ml110G5 to another. Both switches are then interconnected.

1. Write a scheduler (flag `dr`) that ensures fault tolerance to a single switch failure. Balance the replica as possible to minimize the loss in case of failure.

### Fault-tolerance for standalone VMs

When a VM is not replicated (/e.g/ remote desktop scenario), fault-tolerance is obtained by ensuring that if the hosting node crashes, then, it must be possible to restart the VM elsewhere immediatly, on another suitable node. For example, [This figure](figs/1-resilient.png) depicts a viable mapping: if node 1 fails, VM1 can be restarted to N3, if node 2 fails, VM2 can be restarted to N3 and VM1 to N1. Finally, if N3 fails, VM4 can be restarted to N1. [This figure](figs/0-resilient.png) is not fully resilient: if N2 crashes, it is not possible to restart VM2 elsewhere.

1. Implement a new scheduler (`ft` flag) that ensures the fault tolerance to 1 node failure for all the VM having an id that is a multiple of 10.

2. How can we report the infrastructure load in that particular context ?
 
## Load balancing

1. Develop a scheduler that performs load balancing using a [next fit algorithm](http://lmgtfy.com/?q=next+fit+algorithm) (flag `nextFit`). You should observe fewer penalties with regards to the naive scheduler.
1. Develop another algorithm based on a /worst fit algorithm/ (`worstFit` flag) that balances with regards to both RAM and mips. Justify the method you choosed to consider the two dimensions and an evaluation metric. It is ok to work in a pragmatic manner (different approaches, keep the best) at the moment you prove your statements.
1. Which algorithms performs the best in terms of reducing the SLA violation. Why ?

## Performance satisfaction

For a practical understanding of what a SLA violation is in this project, look at the `Revenue` class. Basically, there is a SLA violation when the associated Vm is requiring more MIPS it is possible to get on its host.
If the SLA is not met then the provider must pay penalties to the client. It is then not desirable to have violations to attract customers and maximize the revenues.

1. Implement a scheduler that ensures there can be no SLA violation (`noViolations` flag). Remember the nature of the hypervisor in terms of CPU allocation and the VM templates. The scheduler is effective when you can successfully simulate all the days, with the `Revenue` class reporting no re-fundings due to SLA violation.

## Energy-efficient schedulers

Develop a scheduler (`energy` flag) that reduces the overall energy consumption without relying on VM migration. The resulting simulation must consumes less energy than all the previous schedulers.

## Greedy scheduler

Develop a scheduler that maximizes revenues. It is then important to provide a good trade-off between energy savings and penalties for SLA violation. Justify your choices and the theoretical complexity of the algorithm



#Vicc report: homemade VM Schedulers

This project aims at developing different VM schedulers for a given IaaS cloud. Each scheduler has meaningful properties for either the cloud customers or the cloud provider.

The implementation and the evaluation were made over the IaaS cloud simulator CloudSim. The simulator replays a workload extracted from Emulab, on a datacenter having realistic characteristics.

##Team
Justin Vailhere : justin.vailhere@gmail.com  
Benoit Arliaud : arliaud.benoit@gmail.com  
Jiawen Fan : jiawen.gmd@gmail.com  

##Dev environment
Java 8  
Maven  
IDE : Intellij IDEA  
Git  

To setup our environment , we first cloned the following directory directly in IntelliJ IDEA:
```sh
$ tree
 |- src #the source code
 |- repository #external dependencies
 |- planetlab #the workload to process
 |-cloudsim-3.0.3-src.tar.gz # simulator sources
 \- pom.xml # maven project descriptor
```
Then we checked that everything was working by typing mvn install in the root directory.

##Testing part
`fr.unice.vicc.Main` is the entry point of our application. It can be launched from IntelliJ or using the command `mvn compile exec:java`.

```sh
Usage: Main scheduler [day]
```

- `scheduler` is the identifier of the scheduler to test, prefixed by `--`.
- `day` is optional, it is one of the workload day (see folders in `planetlab`). When `all` is indicated all the days are replayed sequentially.

By default, the output is written in a log file in the `logs` folder.

If we execute the program through `mvn exec:java`, then the arguments are provided using the 'sched' and the 'day' properties.

- To execute the simulator using the `naive` scheduler and all the days:
`mvn compile exec:java -Dsched=naive -Dday=all`
- to replay only day `20110303`: `mvn compile exec:java -Dsched=naive -Dday=20110303`

##Exercises

We developed 8 VM schedulers and use observers to monitor their behaviors.
The class `VmAllocationPolicyFactory` is the entry point to declare our schedulers and integrate them within the codebase.

###Naive scheduler (flag `naive`)
Role: This first scheduler is the most basic, it just distribute VM on hosts until they are full, then it goes to the next host. 
Technical choices: We use the method vmCreate of the class Host to test if there is enough memory and cpu. Then we put each vm assign to a host in the hash map hoster.
Temporal complexity: o(n) where n is the number of host.

####Results
Incomes: 12398,59 €  
Penalties: 402,16 €  
Energy: 2645,63 €  
Revenue: 9350,80 €  
------------------------------------------------------------------------------------------------------------------

###Fault-tolerance for replicated applications
Role: This scheduler aims to prevent failure from a node by replicating VM on distinct nodes and never lose a VM.
Technical choices: We add a variable in the class which is a hashmap. It contains an integer associated with a list of host. These hosts contain all the vm which have the same range id of the input (which own the same hundred of id : 0-99).
Temporal complexity: o(n) where n is the number of host concerned in the hash map.

What is the impact of such an algorithm over the cluster hosting capacity ? Why ?

Such algorithm will reduce the cluster hosting capacity because it requires more resources in memory and more cpu (additional constraint), since the VMs run replicated applications on distinct hosts.

####Results

Incomes:    12398,59€  
Penalties:  200,95€  
Energy:     2688,44€  
Revenue:    9509,21€  

------------------------------------------------------------------------------------------------------------------
###Disaster recovery (flag `dr`)

Role: The aim is to prevent failure which came from the switch. If the hosts are connected with at least 2 switch, then it could stand a failure switch. It must provide an organization with a way to recover data or implement failover in the event of a man-made or natural catastrophe.
Technical choices: We try to balance the VM in the buffer between the two scheduler, the VM will be alternatively allocate between the first and the second switch.
Temporal complexity: o(n) with n the number of host.

####Results
Incomes: 12398.59€  
Penalties: 2223.24€  
Energy: 2649.07€  
Revenue:  7526.28€  
------------------------------------------------------------------------------------------------------------------
###Fault-tolerance for standalone VMs (flag `ft`)

Role: When a failure happens on a node, this scheduler restart all the vm of this node on the remaining nodes. Although no VM are replicated, it is fault tolerance but in a different way. 
Technical choices: We test if the id of the VM end by zero and if the host is suitable for the VM and put it in the host. Otherwise we just put it in a host as soon as possible.
Temporal complexity: o(n) with n the number of host.

How can we report the infrastructure load in that particular context ?
Since we assign one VM out of ten on the most suitable host, we optimize the resources for some node which lead to reduce the energy consumption.

####Results
Incomes: 12398.59€  
Penalties: 215.53€  
Energy:2644.49 €  
Revenue: 9538.57 €  

------------------------------------------------------------------------------------------------------------------
###Load balancing (flag `nextfit`)

Role: Avoid to alter specific hosts prematurely. It is also convenient to minimize the probability of saturating a host. It allows cloud computing to "scale up to increasing demands" by efficiently allocating dynamic local workload evenly across all nodes. This algorithm is an evolution of the First Fit algorithm  except that we start searching from where it left off and not from the beginning. The advantage of the algorithm it’s the fastest one as it searches as little as possible. But there is a loss of unused memory.
Technical choices: next fit algorithm / worst fit algorithm
Temporal complexity: o(nm) with n the number of hosts and m the number of VM.

####Results

Incomes: 12398,59 €  
Penalties: 346,75 €  
Energy: 2715,76 €  
Revenue:  9336,07 €  

------------------------------------------------------------------------------------------------------------------
###Load balancing (flag `worstfit`)

Role: The approach here is to locate available free portion that are large enough to be still useful after putting the VM. The advantage is it reduces the production of small gap. But at later stage it will require larger  memory.
Technical choices: We sort the hosts by the availability of MIPS. Then we take the first one of this list (the one with the greater resources available) and put the VM inside.
Temporal complexity:o(nm) with n the number of hosts and m the number of VM.have sorted

Which algorithms performs the best in terms of reducing the SLA violation Why ?
Worst-Fit scheduler algorithm perform the best 6.06€ against 346.75€. This is because in the Worst-Fit algorithm, the host are sorted by resources available. As a result the VM are always running on hosts with a lot of MIPS available and reduce the SLA violation.

####Results

Incomes: 12398,59 €  
Penalties: 6,06 €  
Energy: 3285,97 €  
Revenue:  9106,56 €  

------------------------------------------------------------------------------------------------------------------
###Performance satisfaction (flag `noViolations`)

Role: This scheduler is made for respecting the SLA violation which bring penalties for the provider. Then it never provide under a threshold of MIPS. As a result it leads to get no penalties.
Technical choices: We made two loop, one for the host and inside another one for the cores of CPU. Then we test if the host can provide enough MIPS compared to the VM need otherwise we search another host. When we found one we attribute the VM to that host.
Temporal complexity: Consequently o(pq) with p the number of hosts and q the number of cores of CPU in each host.

####Results

Incomes: 12398,59 €  
Penalties: 0,00 €  
Energy: 2868,74 €  
Revenue:  9529,85 €  

------------------------------------------------------------------------------------------------------------------
###Energy-efficient (flag `energy`)

Role: This Scheduler aim to reduce as much as possible the number of node without proceed to VM migration and it lead to reduce the energy consumption.
Technical choices: We choose to sort the list of host by the available MIPS. Then we do a loop over this list and assign the first VM to the host with the greatest MIPS available. At the end it contribute to reduce the number of host where a VM is running and then reduce the energy cost.
Temporal complexity: o(nlog(n)) with n the number of hosts.

####Results

Incomes: 12398,59 €  
Penalties: 1413,50 €  
Energy: 2604,30 €  
Revenue:  8380,79 €  

------------------------------------------------------------------------------------------------------------------
###Greedy (flag `greedy`)

Role: The aim is to maximize the benefit and it leads find the best trade-off between first reduce the consumption energy and secondly reduce the costs from SLA violation. 
Technical choices: We start from the code of SLA violation as it was the most profitable algorithm. Then we mix it with the energies scheduler to try to reduce the cost of energy without increasing a lot the penalties.We found the margin of 500 MIPS by testing quite a lot of value (no theory found why this value is the best). To do so we sort the machine by the MIPS available.
Temporal complexity: o(nlog(n)) with n the number of host if log(n) < number of cores of CPU per host.

####Results

Incomes: 12398,59 €  
Penalties: 7,24 €  
Energy: 2754,93 €  
Revenue:  9636,42 €  
