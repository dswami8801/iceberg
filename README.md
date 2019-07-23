## Welcome to IceBerg!
![enter image description here](https://ik.imagekit.io/be4swnsmo/ice_berg_HPOwN1Ipt.jpg?tr=w-200,h-200,c-at_max,oit-false,q-80)
### What Is It?
#### A Distributed InMemory Key-Value Store 
IceBerg is a distributed InMemory Key-Value Store. If we have a load which can't be handled by a single machine then we can start IceBerg's on several machines forming a cluster and they all can store the load across the nodes.
A node can simply join the cluster by knowing the master node's address.
### How It Works ?

#### Cluster 
Multiple nodes joining and participating in the same system forms a cluster , on joining a new node the master node assigns the hash range value for that node , all the keys which falls under that hash range will be stored on that node. 

It's designed based on Leader-follower pattern where for each type of  key-value collections there will be one leader and multiple followers.
Nodes in the cluster check the liveliness by using Gossip heartbeat protocol , 
every node sends heartbeat to majority of other nodes. If any node failed to respond to the heartbeat due to network partition or crashed , the node will be marked dead but won't be removed Immediatly from the cluster , after reaching a failure threshold the node will be removed and hash range will be reallocated for the reaming nodes in the cluster.

#### Write
A write request can be made to any node in the cluster but only the Leader for that partition will be responsible for writing , write request will be routed to the leader internall since every node is aware of the elected leader for all the partiotions.
Once the Leader get the write request It Immediatly writes the data for It's partiotion and sends the replication write request (Selects follower nodes based on replication factor) to few other nodes (followers) in the cluster.
#### Read
A Read request can be served by any node in the cluster , there could be consistency Issues but we are favouring Availability over Consistency.
   
 ##### Improvements In Progress
 ###### Distribute the load evenly among the nodes
 ###### System must resilinet for network partition
 ###### For a failiure of node re-distribute the load evenly
 
 

   

  
