Example: Apache Ignite
======================

### Run

```console
$ docker-compose up
```

### Limitation

Currently, the MBeans `org.apache:group=views` are not supported, because the getAttributes() returns a list of `Object`, not a list of `Attribute`
and we don't handle that well.

### Response Example

```console
$ curl -s http://localhost:9639/metrics | grep org_apache_ignite | grep -v HELP
org_apache_ignite_AlwaysFailoverSpi_MaximumFailoverAttempts{name="AlwaysFailoverSpi",clsLdr="18b4aac2",group="SPIs",} 5
org_apache_ignite_AlwaysFailoverSpi_StartTimestamp{name="AlwaysFailoverSpi",clsLdr="18b4aac2",group="SPIs",} 1596892629209
org_apache_ignite_AlwaysFailoverSpi_TotalFailoverJobsCount{name="AlwaysFailoverSpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_AlwaysFailoverSpi_UpTime{name="AlwaysFailoverSpi",clsLdr="18b4aac2",group="SPIs",} 526515
org_apache_ignite_BaselineAutoAdjust_AutoAdjustmentEnabled{name="BaselineAutoAdjustMXBeanImpl",clsLdr="18b4aac2",group="Baseline",} 1
org_apache_ignite_BaselineAutoAdjust_AutoAdjustmentTimeout{name="BaselineAutoAdjustMXBeanImpl",clsLdr="18b4aac2",group="Baseline",} 0
org_apache_ignite_BaselineAutoAdjust_TimeUntilAutoAdjust{name="BaselineAutoAdjustMXBeanImpl",clsLdr="18b4aac2",group="Baseline",} -1
org_apache_ignite_ClusterLocalNode_ActiveBaselineNodes{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_ClusterLocalNode_AverageActiveJobs{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_AverageCancelledJobs{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_AverageCpuLoad{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0.14383916990920878
org_apache_ignite_ClusterLocalNode_AverageJobExecuteTime{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_AverageJobWaitTime{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_AverageRejectedJobs{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_AverageWaitingJobs{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_BusyTimePercentage{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0.7393777370452881
org_apache_ignite_ClusterLocalNode_CurrentActiveJobs{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_CurrentCancelledJobs{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_CurrentCpuLoad{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0.06666666666666667
org_apache_ignite_ClusterLocalNode_CurrentDaemonThreadCount{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 15
org_apache_ignite_ClusterLocalNode_CurrentGcCpuLoad{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_CurrentIdleTime{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 525047
org_apache_ignite_ClusterLocalNode_CurrentJobExecuteTime{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_CurrentJobWaitTime{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_CurrentPmeDuration{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_CurrentRejectedJobs{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_CurrentThreadCount{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 72
org_apache_ignite_ClusterLocalNode_CurrentWaitingJobs{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_HeapMemoryCommitted{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 689963008
org_apache_ignite_ClusterLocalNode_HeapMemoryInitialized{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1052770304
org_apache_ignite_ClusterLocalNode_HeapMemoryMaximum{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 14969470976
org_apache_ignite_ClusterLocalNode_HeapMemoryTotal{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 14969470976
org_apache_ignite_ClusterLocalNode_HeapMemoryUsed{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 234245952
org_apache_ignite_ClusterLocalNode_IdleTimePercentage{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 99.2606201171875
org_apache_ignite_ClusterLocalNode_LastDataVersion{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1596892629320
org_apache_ignite_ClusterLocalNode_LastUpdateTime{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1596893155724
org_apache_ignite_ClusterLocalNode_MaximumActiveJobs{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_MaximumCancelledJobs{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_MaximumJobExecuteTime{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_MaximumJobWaitTime{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_MaximumRejectedJobs{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_MaximumThreadCount{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 80
org_apache_ignite_ClusterLocalNode_MaximumWaitingJobs{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_NodeStartTime{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1596892629663
org_apache_ignite_ClusterLocalNode_NonHeapMemoryCommitted{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 77201408
org_apache_ignite_ClusterLocalNode_NonHeapMemoryInitialized{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 2555904
org_apache_ignite_ClusterLocalNode_NonHeapMemoryMaximum{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} -1
org_apache_ignite_ClusterLocalNode_NonHeapMemoryTotal{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} -1
org_apache_ignite_ClusterLocalNode_NonHeapMemoryUsed{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 74163904
org_apache_ignite_ClusterLocalNode_OutboundMessagesQueueSize{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_ReceivedBytesCount{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_ReceivedMessagesCount{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_SentBytesCount{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_SentMessagesCount{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_StartTime{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1596892626841
org_apache_ignite_ClusterLocalNode_TopologyVersion{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_ClusterLocalNode_TotalBaselineNodes{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_ClusterLocalNode_TotalBusyTime{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 3911
org_apache_ignite_ClusterLocalNode_TotalCancelledJobs{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_TotalClientNodes{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_TotalCpus{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 6
org_apache_ignite_ClusterLocalNode_TotalExecutedJobs{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_TotalExecutedTasks{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_TotalIdleTime{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 525047
org_apache_ignite_ClusterLocalNode_TotalJobsExecutionTime{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_TotalNodes{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_ClusterLocalNode_TotalRejectedJobs{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_TotalServerNodes{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_ClusterLocalNode_TotalStartedThreadCount{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 134
org_apache_ignite_ClusterLocalNode_UpTime{name="ClusterLocalNodeMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 528957
org_apache_ignite_Cluster_ActiveBaselineNodes{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_Cluster_AverageActiveJobs{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_AverageCancelledJobs{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_AverageCpuLoad{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0.06666666666666667
org_apache_ignite_Cluster_AverageJobExecuteTime{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_AverageJobWaitTime{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_AverageRejectedJobs{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_AverageWaitingJobs{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_BusyTimePercentage{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0.7390022277832031
org_apache_ignite_Cluster_CurrentActiveJobs{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_CurrentCancelledJobs{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_CurrentCpuLoad{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_CurrentDaemonThreadCount{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 15
org_apache_ignite_Cluster_CurrentGcCpuLoad{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_CurrentIdleTime{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 525047
org_apache_ignite_Cluster_CurrentJobExecuteTime{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_CurrentJobWaitTime{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_CurrentPmeDuration{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_CurrentRejectedJobs{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_CurrentThreadCount{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 72
org_apache_ignite_Cluster_CurrentWaitingJobs{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_HeapMemoryCommitted{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 689963008
org_apache_ignite_Cluster_HeapMemoryInitialized{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1052770304
org_apache_ignite_Cluster_HeapMemoryMaximum{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 14969470976
org_apache_ignite_Cluster_HeapMemoryTotal{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 14969470976
org_apache_ignite_Cluster_HeapMemoryUsed{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 234245952
org_apache_ignite_Cluster_IdleTimePercentage{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 99.26100158691406
org_apache_ignite_Cluster_LastDataVersion{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1596892629320
org_apache_ignite_Cluster_LastUpdateTime{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1596893155724
org_apache_ignite_Cluster_MaximumActiveJobs{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_MaximumCancelledJobs{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_MaximumJobExecuteTime{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_MaximumJobWaitTime{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_MaximumRejectedJobs{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_MaximumThreadCount{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 72
org_apache_ignite_Cluster_MaximumWaitingJobs{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_NodeStartTime{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1596892629663
org_apache_ignite_Cluster_NonHeapMemoryCommitted{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 77201408
org_apache_ignite_Cluster_NonHeapMemoryInitialized{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 2555904
org_apache_ignite_Cluster_NonHeapMemoryMaximum{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_NonHeapMemoryTotal{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} -1
org_apache_ignite_Cluster_NonHeapMemoryUsed{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 74163904
org_apache_ignite_Cluster_OutboundMessagesQueueSize{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_ReceivedBytesCount{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_ReceivedMessagesCount{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_SentBytesCount{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_SentMessagesCount{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_StartTime{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1596892626841
org_apache_ignite_Cluster_TopologyVersion{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_Cluster_TotalBaselineNodes{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_Cluster_TotalBusyTime{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 3909
org_apache_ignite_Cluster_TotalCancelledJobs{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_TotalClientNodes{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_TotalCpus{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 6
org_apache_ignite_Cluster_TotalExecutedJobs{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_TotalExecutedTasks{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_TotalIdleTime{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 525047
org_apache_ignite_Cluster_TotalJobsExecutionTime{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_TotalNodes{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_Cluster_TotalRejectedJobs{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_TotalServerNodes{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_Cluster_TotalStartedThreadCount{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 134
org_apache_ignite_Cluster_UpTime{name="ClusterMetricsMXBeanImpl",clsLdr="18b4aac2",group="Kernal",} 528956
org_apache_ignite_DataRegion_AllocationRate{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_AllocationRate{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_AllocationRate{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_CheckpointBufferSize{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_CheckpointBufferSize{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_CheckpointBufferSize{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_DirtyPages{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_DirtyPages{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_DirtyPages{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_EvictionRate{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_EvictionRate{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_EvictionRate{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_InitialSize{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 40
org_apache_ignite_DataRegion_InitialSize{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 256
org_apache_ignite_DataRegion_InitialSize{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 40
org_apache_ignite_DataRegion_LargeEntriesPagesPercentage{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_LargeEntriesPagesPercentage{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_LargeEntriesPagesPercentage{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_MaxSize{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 100
org_apache_ignite_DataRegion_MaxSize{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 12847
org_apache_ignite_DataRegion_MaxSize{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 100
org_apache_ignite_DataRegion_OffHeapSize{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 41943040
org_apache_ignite_DataRegion_OffHeapSize{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_OffHeapSize{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 41943040
org_apache_ignite_DataRegion_OffheapUsedSize{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_OffheapUsedSize{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_OffheapUsedSize{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PageSize{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PageSize{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PageSize{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesFillFactor{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesFillFactor{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesFillFactor{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesRead{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesRead{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesRead{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesReplaceAge{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesReplaceAge{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesReplaceAge{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesReplaceRate{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesReplaceRate{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesReplaceRate{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesReplaced{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesReplaced{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesReplaced{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesWritten{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesWritten{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PagesWritten{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PhysicalMemoryPages{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PhysicalMemoryPages{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PhysicalMemoryPages{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 200
org_apache_ignite_DataRegion_PhysicalMemorySize{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PhysicalMemorySize{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PhysicalMemorySize{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 824000
org_apache_ignite_DataRegion_TotalAllocatedPages{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_TotalAllocatedPages{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_TotalAllocatedPages{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 200
org_apache_ignite_DataRegion_TotalAllocatedSize{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_TotalAllocatedSize{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_TotalAllocatedSize{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 824000
org_apache_ignite_DataRegion_TotalUsedPages{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_TotalUsedPages{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_TotalUsedPages{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 200
org_apache_ignite_DataRegion_UsedCheckpointBufferPages{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_UsedCheckpointBufferPages{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_UsedCheckpointBufferPages{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_UsedCheckpointBufferSize{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_UsedCheckpointBufferSize{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_UsedCheckpointBufferSize{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataStorage_WalCompactionLevel{name="DataStorageMXBeanImpl",clsLdr="18b4aac2",group="DataStorage",} 1
org_apache_ignite_FailureHandling_CheckpointReadLockTimeout{name="FailureHandlingMxBeanImpl",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_FailureHandling_LivenessCheckEnabled{name="FailureHandlingMxBeanImpl",clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_FailureHandling_SystemWorkerBlockedTimeout{name="FailureHandlingMxBeanImpl",clsLdr="18b4aac2",group="Kernal",} 10000
org_apache_ignite_IgniteKernal_LongJVMPausesCount{name="IgniteKernal",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_IgniteKernal_LongJVMPausesTotalDuration{name="IgniteKernal",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_IgniteKernal_NodeInBaseline{name="IgniteKernal",clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_IgniteKernal_PeerClassLoadingEnabled{name="IgniteKernal",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_IgniteKernal_RebalanceEnabled{name="IgniteKernal",clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_IgniteKernal_StartTimestamp{name="IgniteKernal",clsLdr="18b4aac2",group="Kernal",} 1596892631191
org_apache_ignite_IgniteKernal_UpTime{name="IgniteKernal",clsLdr="18b4aac2",group="Kernal",} 524533
org_apache_ignite_LocalDeploymentSpi_StartTimestamp{name="LocalDeploymentSpi",clsLdr="18b4aac2",group="SPIs",} 1596892629199
org_apache_ignite_LocalDeploymentSpi_UpTime{name="LocalDeploymentSpi",clsLdr="18b4aac2",group="SPIs",} 526525
org_apache_ignite_RoundRobinLoadBalancingSpi_PerTask{name="RoundRobinLoadBalancingSpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_RoundRobinLoadBalancingSpi_StartTimestamp{name="RoundRobinLoadBalancingSpi",clsLdr="18b4aac2",group="SPIs",} 1596892629209
org_apache_ignite_RoundRobinLoadBalancingSpi_UpTime{name="RoundRobinLoadBalancingSpi",clsLdr="18b4aac2",group="SPIs",} 526515
org_apache_ignite_SqlQuery_LongQueryTimeoutMultiplier{name="SqlQueryMXBeanImpl",clsLdr="18b4aac2",group="SQL Query",} 2
org_apache_ignite_SqlQuery_LongQueryWarningTimeout{name="SqlQueryMXBeanImpl",clsLdr="18b4aac2",group="SQL Query",} 3000
org_apache_ignite_StripedExecutor_ActiveCount{name="StripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_Shutdown{name="StripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesActiveStatuses{name="StripedExecutor",index="0",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesActiveStatuses{name="StripedExecutor",index="1",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesActiveStatuses{name="StripedExecutor",index="2",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesActiveStatuses{name="StripedExecutor",index="3",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesActiveStatuses{name="StripedExecutor",index="4",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesActiveStatuses{name="StripedExecutor",index="5",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesActiveStatuses{name="StripedExecutor",index="6",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesActiveStatuses{name="StripedExecutor",index="7",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesCompletedTasksCounts{name="StripedExecutor",index="0",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesCompletedTasksCounts{name="StripedExecutor",index="1",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesCompletedTasksCounts{name="StripedExecutor",index="2",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesCompletedTasksCounts{name="StripedExecutor",index="3",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesCompletedTasksCounts{name="StripedExecutor",index="4",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesCompletedTasksCounts{name="StripedExecutor",index="5",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesCompletedTasksCounts{name="StripedExecutor",index="6",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesCompletedTasksCounts{name="StripedExecutor",index="7",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesCount{name="StripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} 8
org_apache_ignite_StripedExecutor_StripesQueueSizes{name="StripedExecutor",index="0",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesQueueSizes{name="StripedExecutor",index="1",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesQueueSizes{name="StripedExecutor",index="2",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesQueueSizes{name="StripedExecutor",index="3",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesQueueSizes{name="StripedExecutor",index="4",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesQueueSizes{name="StripedExecutor",index="5",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesQueueSizes{name="StripedExecutor",index="6",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_StripesQueueSizes{name="StripedExecutor",index="7",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_Terminated{name="StripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_TotalCompletedTasksCount{name="StripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_StripedExecutor_TotalQueueSize{name="StripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_TcpCommunicationSpi_AckSendThreshold{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 32
org_apache_ignite_TcpCommunicationSpi_ConnectTimeout{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 5000
org_apache_ignite_TcpCommunicationSpi_ConnectionsPerNode{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 1
org_apache_ignite_TcpCommunicationSpi_DirectBuffer{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 1
org_apache_ignite_TcpCommunicationSpi_DirectSendBuffer{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpCommunicationSpi_IdleConnectionTimeout{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 600000
org_apache_ignite_TcpCommunicationSpi_LocalPort{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 47100
org_apache_ignite_TcpCommunicationSpi_LocalPortRange{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 100
org_apache_ignite_TcpCommunicationSpi_MaxConnectTimeout{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 600000
org_apache_ignite_TcpCommunicationSpi_MessageQueueLimit{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpCommunicationSpi_OutboundMessagesQueueSize{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpCommunicationSpi_ReceivedBytesCount{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpCommunicationSpi_ReceivedMessagesCount{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpCommunicationSpi_ReconnectCount{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 10
org_apache_ignite_TcpCommunicationSpi_SelectorSpins{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpCommunicationSpi_SelectorsCount{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 4
org_apache_ignite_TcpCommunicationSpi_SentBytesCount{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpCommunicationSpi_SentMessagesCount{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpCommunicationSpi_SharedMemoryPort{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} -1
org_apache_ignite_TcpCommunicationSpi_SlowClientQueueLimit{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpCommunicationSpi_SocketReceiveBuffer{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 32768
org_apache_ignite_TcpCommunicationSpi_SocketSendBuffer{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 32768
org_apache_ignite_TcpCommunicationSpi_SocketWriteTimeout{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 2000
org_apache_ignite_TcpCommunicationSpi_StartTimestamp{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 1596892629109
org_apache_ignite_TcpCommunicationSpi_TcpNoDelay{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 1
org_apache_ignite_TcpCommunicationSpi_UnacknowledgedMessagesBufferSize{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpCommunicationSpi_UpTime{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 526615
org_apache_ignite_TcpCommunicationSpi_UsePairedConnections{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpDiscoverySpi_AckTimeout{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 5000
org_apache_ignite_TcpDiscoverySpi_AvgMessageProcessingTime{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpDiscoverySpi_ClientMode{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpDiscoverySpi_ConnectionCheckInterval{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 500
org_apache_ignite_TcpDiscoverySpi_CoordinatorSinceTimestamp{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 1596892630917
org_apache_ignite_TcpDiscoverySpi_CurrentTopologyVersion{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 1
org_apache_ignite_TcpDiscoverySpi_IpFinderCleanFrequency{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 60000
org_apache_ignite_TcpDiscoverySpi_JoinTimeout{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpDiscoverySpi_LocalPort{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 47500
org_apache_ignite_TcpDiscoverySpi_LocalPortRange{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 100
org_apache_ignite_TcpDiscoverySpi_MaxAckTimeout{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 600000
org_apache_ignite_TcpDiscoverySpi_MaxMessageProcessingTime{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 11
org_apache_ignite_TcpDiscoverySpi_MessageWorkerQueueSize{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpDiscoverySpi_NetworkTimeout{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 5000
org_apache_ignite_TcpDiscoverySpi_NodesFailed{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpDiscoverySpi_NodesJoined{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpDiscoverySpi_NodesLeft{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpDiscoverySpi_PendingMessagesDiscarded{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpDiscoverySpi_PendingMessagesRegistered{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 6
org_apache_ignite_TcpDiscoverySpi_ProcessedMessages{name="TcpDiscoverySpi",key="TcpDiscoveryMetricsUpdateMessage",clsLdr="18b4aac2",group="SPIs",} 262
org_apache_ignite_TcpDiscoverySpi_ProcessedMessages{name="TcpDiscoverySpi",key="TcpDiscoveryCustomEventMessage",clsLdr="18b4aac2",group="SPIs",} 3
org_apache_ignite_TcpDiscoverySpi_ProcessedMessages{name="TcpDiscoverySpi",key="TcpDiscoveryDiscardMessage",clsLdr="18b4aac2",group="SPIs",} 6
org_apache_ignite_TcpDiscoverySpi_ReconnectCount{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 10
org_apache_ignite_TcpDiscoverySpi_SocketTimeout{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 5000
org_apache_ignite_TcpDiscoverySpi_StartTimestamp{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpDiscoverySpi_StatisticsPrintFrequency{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpDiscoverySpi_ThreadPriority{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 10
org_apache_ignite_TcpDiscoverySpi_TotalProcessedMessages{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 271
org_apache_ignite_TcpDiscoverySpi_TotalReceivedMessages{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpDiscoverySpi_UpTime{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_ThreadPool_ActiveCount{name="GridIgfsExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_ActiveCount{name="GridCallbackExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_ActiveCount{name="GridRebalanceExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_ActiveCount{name="GridRestExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_ActiveCount{name="GridSystemExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_ActiveCount{name="GridAffinityExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_ActiveCount{name="GridServicesExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_ActiveCount{name="GridQueryExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_ActiveCount{name="GridClassLoadingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_ActiveCount{name="GridRebalanceStripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_ActiveCount{name="GridSchemaExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_ActiveCount{name="GridExecutionExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_ActiveCount{name="GridIndexingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_ActiveCount{name="GridManagementExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_ActiveCount{name="GridDataStreamExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_ActiveCount{name="GridUtilityCacheExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridIgfsExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridCallbackExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridRebalanceExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridRestExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridSystemExecutor",clsLdr="18b4aac2",group="Thread Pools",} 61
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridAffinityExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridServicesExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridQueryExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridClassLoadingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridRebalanceStripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridSchemaExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridExecutionExecutor",clsLdr="18b4aac2",group="Thread Pools",} 6
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridIndexingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridManagementExecutor",clsLdr="18b4aac2",group="Thread Pools",} 1
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridDataStreamExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridUtilityCacheExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_CorePoolSize{name="GridIgfsExecutor",clsLdr="18b4aac2",group="Thread Pools",} 6
org_apache_ignite_ThreadPool_CorePoolSize{name="GridCallbackExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_CorePoolSize{name="GridRebalanceExecutor",clsLdr="18b4aac2",group="Thread Pools",} 4
org_apache_ignite_ThreadPool_CorePoolSize{name="GridRestExecutor",clsLdr="18b4aac2",group="Thread Pools",} 8
org_apache_ignite_ThreadPool_CorePoolSize{name="GridSystemExecutor",clsLdr="18b4aac2",group="Thread Pools",} 8
org_apache_ignite_ThreadPool_CorePoolSize{name="GridAffinityExecutor",clsLdr="18b4aac2",group="Thread Pools",} 1
org_apache_ignite_ThreadPool_CorePoolSize{name="GridServicesExecutor",clsLdr="18b4aac2",group="Thread Pools",} 8
org_apache_ignite_ThreadPool_CorePoolSize{name="GridQueryExecutor",clsLdr="18b4aac2",group="Thread Pools",} 8
org_apache_ignite_ThreadPool_CorePoolSize{name="GridClassLoadingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 2
org_apache_ignite_ThreadPool_CorePoolSize{name="GridRebalanceStripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_CorePoolSize{name="GridSchemaExecutor",clsLdr="18b4aac2",group="Thread Pools",} 2
org_apache_ignite_ThreadPool_CorePoolSize{name="GridExecutionExecutor",clsLdr="18b4aac2",group="Thread Pools",} 8
org_apache_ignite_ThreadPool_CorePoolSize{name="GridIndexingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 6
org_apache_ignite_ThreadPool_CorePoolSize{name="GridManagementExecutor",clsLdr="18b4aac2",group="Thread Pools",} 4
org_apache_ignite_ThreadPool_CorePoolSize{name="GridDataStreamExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_CorePoolSize{name="GridUtilityCacheExecutor",clsLdr="18b4aac2",group="Thread Pools",} 8
org_apache_ignite_ThreadPool_KeepAliveTime{name="GridIgfsExecutor",clsLdr="18b4aac2",group="Thread Pools",} 60000
org_apache_ignite_ThreadPool_KeepAliveTime{name="GridCallbackExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_KeepAliveTime{name="GridRebalanceExecutor",clsLdr="18b4aac2",group="Thread Pools",} 60000
org_apache_ignite_ThreadPool_KeepAliveTime{name="GridRestExecutor",clsLdr="18b4aac2",group="Thread Pools",} 60000
org_apache_ignite_ThreadPool_KeepAliveTime{name="GridSystemExecutor",clsLdr="18b4aac2",group="Thread Pools",} 60000
org_apache_ignite_ThreadPool_KeepAliveTime{name="GridAffinityExecutor",clsLdr="18b4aac2",group="Thread Pools",} 60000
org_apache_ignite_ThreadPool_KeepAliveTime{name="GridServicesExecutor",clsLdr="18b4aac2",group="Thread Pools",} 60000
org_apache_ignite_ThreadPool_KeepAliveTime{name="GridQueryExecutor",clsLdr="18b4aac2",group="Thread Pools",} 60000
org_apache_ignite_ThreadPool_KeepAliveTime{name="GridClassLoadingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 60000
org_apache_ignite_ThreadPool_KeepAliveTime{name="GridRebalanceStripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_KeepAliveTime{name="GridSchemaExecutor",clsLdr="18b4aac2",group="Thread Pools",} 60000
org_apache_ignite_ThreadPool_KeepAliveTime{name="GridExecutionExecutor",clsLdr="18b4aac2",group="Thread Pools",} 60000
org_apache_ignite_ThreadPool_KeepAliveTime{name="GridIndexingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 3000
org_apache_ignite_ThreadPool_KeepAliveTime{name="GridManagementExecutor",clsLdr="18b4aac2",group="Thread Pools",} 60000
org_apache_ignite_ThreadPool_KeepAliveTime{name="GridDataStreamExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_KeepAliveTime{name="GridUtilityCacheExecutor",clsLdr="18b4aac2",group="Thread Pools",} 60000
org_apache_ignite_ThreadPool_LargestPoolSize{name="GridIgfsExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_LargestPoolSize{name="GridCallbackExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_LargestPoolSize{name="GridRebalanceExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_LargestPoolSize{name="GridRestExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_LargestPoolSize{name="GridSystemExecutor",clsLdr="18b4aac2",group="Thread Pools",} 8
org_apache_ignite_ThreadPool_LargestPoolSize{name="GridAffinityExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_LargestPoolSize{name="GridServicesExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_LargestPoolSize{name="GridQueryExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_LargestPoolSize{name="GridClassLoadingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_LargestPoolSize{name="GridRebalanceStripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_LargestPoolSize{name="GridSchemaExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_LargestPoolSize{name="GridExecutionExecutor",clsLdr="18b4aac2",group="Thread Pools",} 6
org_apache_ignite_ThreadPool_LargestPoolSize{name="GridIndexingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_LargestPoolSize{name="GridManagementExecutor",clsLdr="18b4aac2",group="Thread Pools",} 1
org_apache_ignite_ThreadPool_LargestPoolSize{name="GridDataStreamExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_LargestPoolSize{name="GridUtilityCacheExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_MaximumPoolSize{name="GridIgfsExecutor",clsLdr="18b4aac2",group="Thread Pools",} 6
org_apache_ignite_ThreadPool_MaximumPoolSize{name="GridCallbackExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_MaximumPoolSize{name="GridRebalanceExecutor",clsLdr="18b4aac2",group="Thread Pools",} 4
org_apache_ignite_ThreadPool_MaximumPoolSize{name="GridRestExecutor",clsLdr="18b4aac2",group="Thread Pools",} 8
org_apache_ignite_ThreadPool_MaximumPoolSize{name="GridSystemExecutor",clsLdr="18b4aac2",group="Thread Pools",} 8
org_apache_ignite_ThreadPool_MaximumPoolSize{name="GridAffinityExecutor",clsLdr="18b4aac2",group="Thread Pools",} 1
org_apache_ignite_ThreadPool_MaximumPoolSize{name="GridServicesExecutor",clsLdr="18b4aac2",group="Thread Pools",} 8
org_apache_ignite_ThreadPool_MaximumPoolSize{name="GridQueryExecutor",clsLdr="18b4aac2",group="Thread Pools",} 8
org_apache_ignite_ThreadPool_MaximumPoolSize{name="GridClassLoadingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 2
org_apache_ignite_ThreadPool_MaximumPoolSize{name="GridRebalanceStripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_MaximumPoolSize{name="GridSchemaExecutor",clsLdr="18b4aac2",group="Thread Pools",} 2
org_apache_ignite_ThreadPool_MaximumPoolSize{name="GridExecutionExecutor",clsLdr="18b4aac2",group="Thread Pools",} 8
org_apache_ignite_ThreadPool_MaximumPoolSize{name="GridIndexingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 12
org_apache_ignite_ThreadPool_MaximumPoolSize{name="GridManagementExecutor",clsLdr="18b4aac2",group="Thread Pools",} 4
org_apache_ignite_ThreadPool_MaximumPoolSize{name="GridDataStreamExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_MaximumPoolSize{name="GridUtilityCacheExecutor",clsLdr="18b4aac2",group="Thread Pools",} 8
org_apache_ignite_ThreadPool_PoolSize{name="GridIgfsExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_PoolSize{name="GridCallbackExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_PoolSize{name="GridRebalanceExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_PoolSize{name="GridRestExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_PoolSize{name="GridSystemExecutor",clsLdr="18b4aac2",group="Thread Pools",} 6
org_apache_ignite_ThreadPool_PoolSize{name="GridAffinityExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_PoolSize{name="GridServicesExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_PoolSize{name="GridQueryExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_PoolSize{name="GridClassLoadingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_PoolSize{name="GridRebalanceStripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_PoolSize{name="GridSchemaExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_PoolSize{name="GridExecutionExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_PoolSize{name="GridIndexingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_PoolSize{name="GridManagementExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_PoolSize{name="GridDataStreamExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_PoolSize{name="GridUtilityCacheExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_QueueSize{name="GridIgfsExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_QueueSize{name="GridCallbackExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_QueueSize{name="GridRebalanceExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_QueueSize{name="GridRestExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_QueueSize{name="GridSystemExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_QueueSize{name="GridAffinityExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_QueueSize{name="GridServicesExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_QueueSize{name="GridQueryExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_QueueSize{name="GridClassLoadingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_QueueSize{name="GridRebalanceStripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_QueueSize{name="GridSchemaExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_QueueSize{name="GridExecutionExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_QueueSize{name="GridIndexingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_QueueSize{name="GridManagementExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_QueueSize{name="GridDataStreamExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_QueueSize{name="GridUtilityCacheExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Shutdown{name="GridIgfsExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Shutdown{name="GridCallbackExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Shutdown{name="GridRebalanceExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Shutdown{name="GridRestExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Shutdown{name="GridSystemExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Shutdown{name="GridAffinityExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Shutdown{name="GridServicesExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Shutdown{name="GridQueryExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Shutdown{name="GridClassLoadingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Shutdown{name="GridRebalanceStripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Shutdown{name="GridSchemaExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Shutdown{name="GridExecutionExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Shutdown{name="GridIndexingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Shutdown{name="GridManagementExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Shutdown{name="GridDataStreamExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Shutdown{name="GridUtilityCacheExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_TaskCount{name="GridIgfsExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_TaskCount{name="GridCallbackExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_TaskCount{name="GridRebalanceExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_TaskCount{name="GridRestExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_TaskCount{name="GridSystemExecutor",clsLdr="18b4aac2",group="Thread Pools",} 61
org_apache_ignite_ThreadPool_TaskCount{name="GridAffinityExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_TaskCount{name="GridServicesExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_TaskCount{name="GridQueryExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_TaskCount{name="GridClassLoadingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_TaskCount{name="GridRebalanceStripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_TaskCount{name="GridSchemaExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_TaskCount{name="GridExecutionExecutor",clsLdr="18b4aac2",group="Thread Pools",} 6
org_apache_ignite_ThreadPool_TaskCount{name="GridIndexingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_TaskCount{name="GridManagementExecutor",clsLdr="18b4aac2",group="Thread Pools",} 1
org_apache_ignite_ThreadPool_TaskCount{name="GridDataStreamExecutor",clsLdr="18b4aac2",group="Thread Pools",} -1
org_apache_ignite_ThreadPool_TaskCount{name="GridUtilityCacheExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminated{name="GridIgfsExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminated{name="GridCallbackExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminated{name="GridRebalanceExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminated{name="GridRestExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminated{name="GridSystemExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminated{name="GridAffinityExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminated{name="GridServicesExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminated{name="GridQueryExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminated{name="GridClassLoadingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminated{name="GridRebalanceStripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminated{name="GridSchemaExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminated{name="GridExecutionExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminated{name="GridIndexingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminated{name="GridManagementExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminated{name="GridDataStreamExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminated{name="GridUtilityCacheExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminating{name="GridIgfsExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminating{name="GridCallbackExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminating{name="GridRebalanceExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminating{name="GridRestExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminating{name="GridSystemExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminating{name="GridAffinityExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminating{name="GridServicesExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminating{name="GridQueryExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminating{name="GridClassLoadingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminating{name="GridRebalanceStripedExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminating{name="GridSchemaExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminating{name="GridExecutionExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminating{name="GridIndexingExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminating{name="GridManagementExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminating{name="GridDataStreamExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_ThreadPool_Terminating{name="GridUtilityCacheExecutor",clsLdr="18b4aac2",group="Thread Pools",} 0
org_apache_ignite_Transaction_LockedKeysNumber{name="TransactionMetricsMxBeanImpl",clsLdr="18b4aac2",group="TransactionMetrics",} 0
org_apache_ignite_Transaction_OwnerTransactionsNumber{name="TransactionMetricsMxBeanImpl",clsLdr="18b4aac2",group="TransactionMetrics",} 0
org_apache_ignite_Transaction_TransactionsCommittedNumber{name="TransactionMetricsMxBeanImpl",clsLdr="18b4aac2",group="TransactionMetrics",} 0
org_apache_ignite_Transaction_TransactionsHoldingLockNumber{name="TransactionMetricsMxBeanImpl",clsLdr="18b4aac2",group="TransactionMetrics",} 0
org_apache_ignite_Transaction_TransactionsRolledBackNumber{name="TransactionMetricsMxBeanImpl",clsLdr="18b4aac2",group="TransactionMetrics",} 0
org_apache_ignite_Transactions_LongTransactionTimeDumpThreshold{name="TransactionsMXBeanImpl",clsLdr="18b4aac2",group="Transactions",} 0
org_apache_ignite_Transactions_TransactionTimeDumpSamplesCoefficient{name="TransactionsMXBeanImpl",clsLdr="18b4aac2",group="Transactions",} 0
org_apache_ignite_Transactions_TransactionTimeDumpSamplesPerSecondLimit{name="TransactionsMXBeanImpl",clsLdr="18b4aac2",group="Transactions",} 5
org_apache_ignite_Transactions_TxOwnerDumpRequestsAllowed{name="TransactionsMXBeanImpl",clsLdr="18b4aac2",group="Transactions",} 1
org_apache_ignite_Transactions_TxTimeoutOnPartitionMapExchange{name="TransactionsMXBeanImpl",clsLdr="18b4aac2",group="Transactions",} 0
```
