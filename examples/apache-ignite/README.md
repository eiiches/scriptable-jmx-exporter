Example: Apache Ignite
======================

### Run

```console
$ docker-compose up
```

### Limitation

Currently, this configuration doesn't expose `org.apache:group=views` MBeans, except for `org.apache:name=caches,group=views` and `org.apache:name=cacheGroups,group=views`.

### Response Example

```console
$ curl -s "localhost:9639/metrics?include_timestamp=false&include_help=false" | grep org_apache_ignite
org_apache_ignite_AlwaysFailoverSpi_MaximumFailoverAttempts{name="AlwaysFailoverSpi",clsLdr="18b4aac2",group="SPIs",} 5
org_apache_ignite_AlwaysFailoverSpi_StartTimestamp{name="AlwaysFailoverSpi",clsLdr="18b4aac2",group="SPIs",} 1597000597573
org_apache_ignite_AlwaysFailoverSpi_TotalFailoverJobsCount{name="AlwaysFailoverSpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_AlwaysFailoverSpi_UpTime{name="AlwaysFailoverSpi",clsLdr="18b4aac2",group="SPIs",} 2222502
org_apache_ignite_BaselineAutoAdjust_AutoAdjustmentEnabled{clsLdr="18b4aac2",group="Baseline",} 1
org_apache_ignite_BaselineAutoAdjust_AutoAdjustmentTimeout{clsLdr="18b4aac2",group="Baseline",} 0
org_apache_ignite_BaselineAutoAdjust_TimeUntilAutoAdjust{clsLdr="18b4aac2",group="Baseline",} -1
org_apache_ignite_CacheCluster_AverageGetTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_AverageGetTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_AveragePutTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_AveragePutTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_AverageRemoveTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_AverageRemoveTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_AverageTxCommitTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_AverageTxCommitTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_AverageTxRollbackTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_AverageTxRollbackTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_CacheEvictions{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_CacheEvictions{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_CacheGets{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_CacheGets{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_CacheHitPercentage{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_CacheHitPercentage{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_CacheHits{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_CacheHits{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_CacheMissPercentage{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_CacheMissPercentage{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_CacheMisses{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_CacheMisses{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_CachePuts{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_CachePuts{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_CacheRemovals{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_CacheRemovals{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_CacheSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_CacheSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_CacheTxCommits{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_CacheTxCommits{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_CacheTxRollbacks{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_CacheTxRollbacks{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_DhtEvictQueueCurrentSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_DhtEvictQueueCurrentSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_Empty{clsLdr="18b4aac2",group="default",} 1
org_apache_ignite_CacheCluster_Empty{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_EntryProcessorAverageInvocationTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_EntryProcessorAverageInvocationTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_EntryProcessorHitPercentage{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_EntryProcessorHitPercentage{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_EntryProcessorHits{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_EntryProcessorHits{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_EntryProcessorInvocations{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_EntryProcessorInvocations{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_EntryProcessorMaxInvocationTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_EntryProcessorMaxInvocationTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_EntryProcessorMinInvocationTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_EntryProcessorMinInvocationTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_EntryProcessorMissPercentage{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_EntryProcessorMissPercentage{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_EntryProcessorMisses{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_EntryProcessorMisses{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_EntryProcessorPuts{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_EntryProcessorPuts{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_EntryProcessorReadOnlyInvocations{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_EntryProcessorReadOnlyInvocations{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_EntryProcessorRemovals{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_EntryProcessorRemovals{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_EstimatedRebalancingFinishTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_EstimatedRebalancingFinishTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_EstimatedRebalancingKeys{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_EstimatedRebalancingKeys{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_HeapEntriesCount{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_HeapEntriesCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_KeySize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_KeySize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_KeysToRebalanceLeft{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_KeysToRebalanceLeft{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_ManagementEnabled{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_ManagementEnabled{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_OffHeapAllocatedSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_OffHeapAllocatedSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_OffHeapBackupEntriesCount{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_OffHeapBackupEntriesCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_OffHeapEntriesCount{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_OffHeapEntriesCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_OffHeapEvictions{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_OffHeapEvictions{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_OffHeapGets{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_OffHeapGets{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_OffHeapHitPercentage{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_OffHeapHitPercentage{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_OffHeapHits{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_OffHeapHits{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_OffHeapMissPercentage{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_OffHeapMissPercentage{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_OffHeapMisses{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_OffHeapMisses{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_OffHeapPrimaryEntriesCount{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_OffHeapPrimaryEntriesCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_OffHeapPuts{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_OffHeapPuts{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_OffHeapRemovals{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_OffHeapRemovals{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_ReadThrough{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_ReadThrough{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_RebalanceClearingPartitionsLeft{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_RebalanceClearingPartitionsLeft{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_RebalancedKeys{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_RebalancedKeys{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_RebalancingBytesRate{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_RebalancingBytesRate{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_RebalancingKeysRate{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_RebalancingKeysRate{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_RebalancingPartitionsCount{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_RebalancingPartitionsCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_RebalancingStartTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_RebalancingStartTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_Size{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_Size{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_StatisticsEnabled{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_StatisticsEnabled{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_StoreByValue{clsLdr="18b4aac2",group="default",} 1
org_apache_ignite_CacheCluster_StoreByValue{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 1
org_apache_ignite_CacheCluster_TotalPartitionsCount{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_TotalPartitionsCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_TxCommitQueueSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_TxCommitQueueSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_TxCommittedVersionsSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_TxCommittedVersionsSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_TxDhtCommitQueueSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_TxDhtCommitQueueSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_TxDhtCommittedVersionsSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_TxDhtCommittedVersionsSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_TxDhtPrepareQueueSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_TxDhtPrepareQueueSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_TxDhtRolledbackVersionsSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_TxDhtRolledbackVersionsSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_TxDhtStartVersionCountsSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_TxDhtStartVersionCountsSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_TxDhtThreadMapSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_TxDhtThreadMapSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_TxDhtXidMapSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_TxDhtXidMapSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_TxPrepareQueueSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_TxPrepareQueueSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_TxRolledbackVersionsSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_TxRolledbackVersionsSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_TxStartVersionCountsSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_TxStartVersionCountsSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_TxThreadMapSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_TxThreadMapSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_TxXidMapSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_TxXidMapSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_ValidForReading{clsLdr="18b4aac2",group="default",} 1
org_apache_ignite_CacheCluster_ValidForReading{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 1
org_apache_ignite_CacheCluster_ValidForWriting{clsLdr="18b4aac2",group="default",} 1
org_apache_ignite_CacheCluster_ValidForWriting{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 1
org_apache_ignite_CacheCluster_WriteBehindBufferSize{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheCluster_WriteBehindBufferSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheCluster_WriteBehindCriticalOverflowCount{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_WriteBehindCriticalOverflowCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_WriteBehindEnabled{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_WriteBehindEnabled{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_WriteBehindErrorRetryCount{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_WriteBehindErrorRetryCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_WriteBehindFlushFrequency{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheCluster_WriteBehindFlushFrequency{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheCluster_WriteBehindFlushSize{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheCluster_WriteBehindFlushSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheCluster_WriteBehindFlushThreadCount{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheCluster_WriteBehindFlushThreadCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheCluster_WriteBehindStoreBatchSize{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheCluster_WriteBehindStoreBatchSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheCluster_WriteBehindTotalCriticalOverflowCount{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_WriteBehindTotalCriticalOverflowCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheCluster_WriteThrough{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheCluster_WriteThrough{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheGroupView_views_backups{cacheGroupName="default",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheGroupView_views_backups{cacheGroupName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 2147483647
org_apache_ignite_CacheGroupView_views_backups{cacheGroupName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheGroupView_views_cacheCount{cacheGroupName="default",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheGroupView_views_cacheCount{cacheGroupName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheGroupView_views_cacheCount{cacheGroupName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheGroupView_views_cacheGroupId{cacheGroupName="default",clsLdr="18b4aac2",group="views",} 1544803905
org_apache_ignite_CacheGroupView_views_cacheGroupId{cacheGroupName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} -2100569601
org_apache_ignite_CacheGroupView_views_cacheGroupId{cacheGroupName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} -1680318682
org_apache_ignite_CacheGroupView_views_isShared{cacheGroupName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheGroupView_views_isShared{cacheGroupName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheGroupView_views_isShared{cacheGroupName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheGroupView_views_partitionsCount{cacheGroupName="default",clsLdr="18b4aac2",group="views",} 1024
org_apache_ignite_CacheGroupView_views_partitionsCount{cacheGroupName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 100
org_apache_ignite_CacheGroupView_views_partitionsCount{cacheGroupName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 1024
org_apache_ignite_CacheGroupView_views_rebalanceDelay{cacheGroupName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheGroupView_views_rebalanceDelay{cacheGroupName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheGroupView_views_rebalanceDelay{cacheGroupName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheGroupView_views_rebalanceOrder{cacheGroupName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheGroupView_views_rebalanceOrder{cacheGroupName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} -2
org_apache_ignite_CacheGroupView_views_rebalanceOrder{cacheGroupName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheGroupView_views_systemViewRowId{cacheGroupName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheGroupView_views_systemViewRowId{cacheGroupName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheGroupView_views_systemViewRowId{cacheGroupName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 2
org_apache_ignite_CacheGroup_Backups{name="default",clsLdr="18b4aac2",group="Cache groups",} 1
org_apache_ignite_CacheGroup_Backups{name="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="Cache groups",} 1
org_apache_ignite_CacheGroup_ClusterMovingPartitionsCount{name="default",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheGroup_ClusterMovingPartitionsCount{name="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheGroup_ClusterOwningPartitionsCount{name="default",clsLdr="18b4aac2",group="Cache groups",} 1024
org_apache_ignite_CacheGroup_ClusterOwningPartitionsCount{name="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="Cache groups",} 1024
org_apache_ignite_CacheGroup_GroupId{name="default",clsLdr="18b4aac2",group="Cache groups",} 1544803905
org_apache_ignite_CacheGroup_GroupId{name="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="Cache groups",} -1680318682
org_apache_ignite_CacheGroup_IndexBuildCountPartitionsLeft{name="default",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheGroup_IndexBuildCountPartitionsLeft{name="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheGroup_LocalNodeMovingPartitionsCount{name="default",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheGroup_LocalNodeMovingPartitionsCount{name="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheGroup_LocalNodeOwningPartitionsCount{name="default",clsLdr="18b4aac2",group="Cache groups",} 1024
org_apache_ignite_CacheGroup_LocalNodeOwningPartitionsCount{name="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="Cache groups",} 1024
org_apache_ignite_CacheGroup_LocalNodeRentingEntriesCount{name="default",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheGroup_LocalNodeRentingEntriesCount{name="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheGroup_LocalNodeRentingPartitionsCount{name="default",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheGroup_LocalNodeRentingPartitionsCount{name="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheGroup_MaximumNumberOfPartitionCopies{name="default",clsLdr="18b4aac2",group="Cache groups",} 1
org_apache_ignite_CacheGroup_MaximumNumberOfPartitionCopies{name="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="Cache groups",} 1
org_apache_ignite_CacheGroup_MinimumNumberOfPartitionCopies{name="default",clsLdr="18b4aac2",group="Cache groups",} 1
org_apache_ignite_CacheGroup_MinimumNumberOfPartitionCopies{name="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="Cache groups",} 1
org_apache_ignite_CacheGroup_Partitions{name="default",clsLdr="18b4aac2",group="Cache groups",} 1024
org_apache_ignite_CacheGroup_Partitions{name="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="Cache groups",} 1024
org_apache_ignite_CacheGroup_SparseStorageSize{name="default",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheGroup_SparseStorageSize{name="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheGroup_StorageSize{name="default",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheGroup_StorageSize{name="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheGroup_TotalAllocatedPages{name="default",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheGroup_TotalAllocatedPages{name="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheGroup_TotalAllocatedSize{name="default",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheGroup_TotalAllocatedSize{name="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="Cache groups",} 0
org_apache_ignite_CacheLocal_AverageGetTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_AverageGetTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_AveragePutTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_AveragePutTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_AverageRemoveTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_AverageRemoveTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_AverageTxCommitTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_AverageTxCommitTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_AverageTxRollbackTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_AverageTxRollbackTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_CacheEvictions{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_CacheEvictions{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_CacheGets{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_CacheGets{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_CacheHitPercentage{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_CacheHitPercentage{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_CacheHits{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_CacheHits{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_CacheMissPercentage{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_CacheMissPercentage{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_CacheMisses{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_CacheMisses{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_CachePuts{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_CachePuts{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_CacheRemovals{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_CacheRemovals{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_CacheSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_CacheSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 1
org_apache_ignite_CacheLocal_CacheTxCommits{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_CacheTxCommits{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_CacheTxRollbacks{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_CacheTxRollbacks{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_DhtEvictQueueCurrentSize{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheLocal_DhtEvictQueueCurrentSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheLocal_Empty{clsLdr="18b4aac2",group="default",} 1
org_apache_ignite_CacheLocal_Empty{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_EntryProcessorAverageInvocationTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_EntryProcessorAverageInvocationTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_EntryProcessorHitPercentage{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_EntryProcessorHitPercentage{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_EntryProcessorHits{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_EntryProcessorHits{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_EntryProcessorInvocations{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_EntryProcessorInvocations{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_EntryProcessorMaxInvocationTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_EntryProcessorMaxInvocationTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_EntryProcessorMinInvocationTime{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_EntryProcessorMinInvocationTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_EntryProcessorMissPercentage{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_EntryProcessorMissPercentage{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_EntryProcessorMisses{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_EntryProcessorMisses{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_EntryProcessorPuts{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_EntryProcessorPuts{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_EntryProcessorReadOnlyInvocations{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_EntryProcessorReadOnlyInvocations{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_EntryProcessorRemovals{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_EntryProcessorRemovals{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_EstimatedRebalancingFinishTime{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheLocal_EstimatedRebalancingFinishTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheLocal_EstimatedRebalancingKeys{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_EstimatedRebalancingKeys{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_HeapEntriesCount{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_HeapEntriesCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_KeySize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_KeySize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 1
org_apache_ignite_CacheLocal_KeysToRebalanceLeft{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_KeysToRebalanceLeft{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_ManagementEnabled{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_ManagementEnabled{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_OffHeapAllocatedSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_OffHeapAllocatedSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_OffHeapBackupEntriesCount{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_OffHeapBackupEntriesCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_OffHeapEntriesCount{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_OffHeapEntriesCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 1
org_apache_ignite_CacheLocal_OffHeapEvictions{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_OffHeapEvictions{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_OffHeapGets{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_OffHeapGets{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_OffHeapHitPercentage{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_OffHeapHitPercentage{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_OffHeapHits{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_OffHeapHits{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_OffHeapMissPercentage{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_OffHeapMissPercentage{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_OffHeapMisses{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_OffHeapMisses{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_OffHeapPrimaryEntriesCount{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_OffHeapPrimaryEntriesCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 1
org_apache_ignite_CacheLocal_OffHeapPuts{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_OffHeapPuts{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_OffHeapRemovals{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_OffHeapRemovals{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_ReadThrough{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_ReadThrough{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_RebalanceClearingPartitionsLeft{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_RebalanceClearingPartitionsLeft{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_RebalancedKeys{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_RebalancedKeys{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_RebalancingBytesRate{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_RebalancingBytesRate{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_RebalancingKeysRate{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_RebalancingKeysRate{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_RebalancingPartitionsCount{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_RebalancingPartitionsCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_RebalancingStartTime{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheLocal_RebalancingStartTime{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheLocal_Size{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_Size{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 1
org_apache_ignite_CacheLocal_StatisticsEnabled{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_StatisticsEnabled{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_StoreByValue{clsLdr="18b4aac2",group="default",} 1
org_apache_ignite_CacheLocal_StoreByValue{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 1
org_apache_ignite_CacheLocal_TotalPartitionsCount{clsLdr="18b4aac2",group="default",} 1024
org_apache_ignite_CacheLocal_TotalPartitionsCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 1024
org_apache_ignite_CacheLocal_TxCommitQueueSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_TxCommitQueueSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_TxCommittedVersionsSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_TxCommittedVersionsSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_TxDhtCommitQueueSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_TxDhtCommitQueueSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_TxDhtCommittedVersionsSize{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheLocal_TxDhtCommittedVersionsSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheLocal_TxDhtPrepareQueueSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_TxDhtPrepareQueueSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_TxDhtRolledbackVersionsSize{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheLocal_TxDhtRolledbackVersionsSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheLocal_TxDhtStartVersionCountsSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_TxDhtStartVersionCountsSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_TxDhtThreadMapSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_TxDhtThreadMapSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_TxDhtXidMapSize{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheLocal_TxDhtXidMapSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheLocal_TxPrepareQueueSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_TxPrepareQueueSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_TxRolledbackVersionsSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_TxRolledbackVersionsSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_TxStartVersionCountsSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_TxStartVersionCountsSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_TxThreadMapSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_TxThreadMapSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_TxXidMapSize{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_TxXidMapSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_ValidForReading{clsLdr="18b4aac2",group="default",} 1
org_apache_ignite_CacheLocal_ValidForReading{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 1
org_apache_ignite_CacheLocal_ValidForWriting{clsLdr="18b4aac2",group="default",} 1
org_apache_ignite_CacheLocal_ValidForWriting{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 1
org_apache_ignite_CacheLocal_WriteBehindBufferSize{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheLocal_WriteBehindBufferSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheLocal_WriteBehindCriticalOverflowCount{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheLocal_WriteBehindCriticalOverflowCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheLocal_WriteBehindEnabled{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_WriteBehindEnabled{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheLocal_WriteBehindErrorRetryCount{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheLocal_WriteBehindErrorRetryCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheLocal_WriteBehindFlushFrequency{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheLocal_WriteBehindFlushFrequency{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheLocal_WriteBehindFlushSize{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheLocal_WriteBehindFlushSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheLocal_WriteBehindFlushThreadCount{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheLocal_WriteBehindFlushThreadCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheLocal_WriteBehindStoreBatchSize{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheLocal_WriteBehindStoreBatchSize{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheLocal_WriteBehindTotalCriticalOverflowCount{clsLdr="18b4aac2",group="default",} -1
org_apache_ignite_CacheLocal_WriteBehindTotalCriticalOverflowCount{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} -1
org_apache_ignite_CacheLocal_WriteThrough{clsLdr="18b4aac2",group="default",} 0
org_apache_ignite_CacheLocal_WriteThrough{clsLdr="18b4aac2",group="redis-ignite-internal-cache-0",} 0
org_apache_ignite_CacheView_views_backups{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_backups{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 2147483647
org_apache_ignite_CacheView_views_backups{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_cacheGroupId{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 1544803905
org_apache_ignite_CacheView_views_cacheGroupId{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} -2100569601
org_apache_ignite_CacheView_views_cacheGroupId{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} -1680318682
org_apache_ignite_CacheView_views_cacheId{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 1544803905
org_apache_ignite_CacheView_views_cacheId{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} -2100569601
org_apache_ignite_CacheView_views_cacheId{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} -1680318682
org_apache_ignite_CacheView_views_defaultLockTimeout{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_defaultLockTimeout{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_defaultLockTimeout{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isCopyOnRead{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_isCopyOnRead{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isCopyOnRead{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_isEagerTtl{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_isEagerTtl{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_isEagerTtl{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_isEncryptionEnabled{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isEncryptionEnabled{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isEncryptionEnabled{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isEventsDisabled{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isEventsDisabled{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isEventsDisabled{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isInvalidate{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isInvalidate{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isInvalidate{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isLoadPreviousValue{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isLoadPreviousValue{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isLoadPreviousValue{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isManagementEnabled{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isManagementEnabled{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isManagementEnabled{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isNearCacheEnabled{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isNearCacheEnabled{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isNearCacheEnabled{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isOnheapCacheEnabled{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isOnheapCacheEnabled{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isOnheapCacheEnabled{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isReadFromBackup{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_isReadFromBackup{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_isReadFromBackup{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_isReadThrough{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isReadThrough{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isReadThrough{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isSqlEscapeAll{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isSqlEscapeAll{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isSqlEscapeAll{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isSqlOnheapCacheEnabled{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isSqlOnheapCacheEnabled{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isSqlOnheapCacheEnabled{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isStatisticsEnabled{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isStatisticsEnabled{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isStatisticsEnabled{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isStoreKeepBinary{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isStoreKeepBinary{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isStoreKeepBinary{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isWriteBehindEnabled{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isWriteBehindEnabled{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isWriteBehindEnabled{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isWriteThrough{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isWriteThrough{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_isWriteThrough{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_maxConcurrentAsyncOperations{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 500
org_apache_ignite_CacheView_views_maxConcurrentAsyncOperations{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 500
org_apache_ignite_CacheView_views_maxConcurrentAsyncOperations{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 500
org_apache_ignite_CacheView_views_maxQueryIteratorsCount{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 1024
org_apache_ignite_CacheView_views_maxQueryIteratorsCount{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 1024
org_apache_ignite_CacheView_views_maxQueryIteratorsCount{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 1024
org_apache_ignite_CacheView_views_nearCacheStartSize{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_nearCacheStartSize{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_nearCacheStartSize{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_queryDetailMetricsSize{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_queryDetailMetricsSize{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_queryDetailMetricsSize{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_queryParallelism{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_queryParallelism{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_queryParallelism{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_rebalanceBatchSize{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 524288
org_apache_ignite_CacheView_views_rebalanceBatchSize{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 524288
org_apache_ignite_CacheView_views_rebalanceBatchSize{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 524288
org_apache_ignite_CacheView_views_rebalanceBatchesPrefetchCount{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 3
org_apache_ignite_CacheView_views_rebalanceBatchesPrefetchCount{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 3
org_apache_ignite_CacheView_views_rebalanceBatchesPrefetchCount{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 3
org_apache_ignite_CacheView_views_rebalanceDelay{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_rebalanceDelay{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_rebalanceDelay{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_rebalanceOrder{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_rebalanceOrder{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} -2
org_apache_ignite_CacheView_views_rebalanceOrder{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_rebalanceThrottle{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_rebalanceThrottle{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_rebalanceThrottle{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_rebalanceTimeout{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 10000
org_apache_ignite_CacheView_views_rebalanceTimeout{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 10000
org_apache_ignite_CacheView_views_rebalanceTimeout{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 10000
org_apache_ignite_CacheView_views_sqlIndexMaxInlineSize{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} -1
org_apache_ignite_CacheView_views_sqlIndexMaxInlineSize{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} -1
org_apache_ignite_CacheView_views_sqlIndexMaxInlineSize{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} -1
org_apache_ignite_CacheView_views_sqlOnheapCacheMaxSize{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_sqlOnheapCacheMaxSize{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_sqlOnheapCacheMaxSize{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_systemViewRowId{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 0
org_apache_ignite_CacheView_views_systemViewRowId{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_systemViewRowId{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 2
org_apache_ignite_CacheView_views_writeBehindBatchSize{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 512
org_apache_ignite_CacheView_views_writeBehindBatchSize{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 512
org_apache_ignite_CacheView_views_writeBehindBatchSize{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 512
org_apache_ignite_CacheView_views_writeBehindCoalescing{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_writeBehindCoalescing{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_writeBehindCoalescing{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_writeBehindFlushFrequency{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 5000
org_apache_ignite_CacheView_views_writeBehindFlushFrequency{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 5000
org_apache_ignite_CacheView_views_writeBehindFlushFrequency{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 5000
org_apache_ignite_CacheView_views_writeBehindFlushSize{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 10240
org_apache_ignite_CacheView_views_writeBehindFlushSize{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 10240
org_apache_ignite_CacheView_views_writeBehindFlushSize{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 10240
org_apache_ignite_CacheView_views_writeBehindFlushThreadCount{cacheGroupName="default",cacheName="default",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_writeBehindFlushThreadCount{cacheGroupName="ignite-sys-cache",cacheName="ignite-sys-cache",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_CacheView_views_writeBehindFlushThreadCount{cacheGroupName="redis-ignite-internal-cache-0",cacheName="redis-ignite-internal-cache-0",clsLdr="18b4aac2",group="views",} 1
org_apache_ignite_ClusterLocalNode_ActiveBaselineNodes{clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_ClusterLocalNode_AverageActiveJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_AverageCancelledJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_AverageCpuLoad{clsLdr="18b4aac2",group="Kernal",} 0.19182522903453011
org_apache_ignite_ClusterLocalNode_AverageJobExecuteTime{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_AverageJobWaitTime{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_AverageRejectedJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_AverageWaitingJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_BusyTimePercentage{clsLdr="18b4aac2",group="Kernal",} 0.1531660556793213
org_apache_ignite_ClusterLocalNode_CurrentActiveJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_CurrentCancelledJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_CurrentCpuLoad{clsLdr="18b4aac2",group="Kernal",} 0.03333333333333333
org_apache_ignite_ClusterLocalNode_CurrentDaemonThreadCount{clsLdr="18b4aac2",group="Kernal",} 18
org_apache_ignite_ClusterLocalNode_CurrentGcCpuLoad{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_CurrentIdleTime{clsLdr="18b4aac2",group="Kernal",} 2221616
org_apache_ignite_ClusterLocalNode_CurrentJobExecuteTime{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_CurrentJobWaitTime{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_CurrentPmeDuration{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_CurrentRejectedJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_CurrentThreadCount{clsLdr="18b4aac2",group="Kernal",} 76
org_apache_ignite_ClusterLocalNode_CurrentWaitingJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_HeapMemoryCommitted{clsLdr="18b4aac2",group="Kernal",} 1271398400
org_apache_ignite_ClusterLocalNode_HeapMemoryInitialized{clsLdr="18b4aac2",group="Kernal",} 1052770304
org_apache_ignite_ClusterLocalNode_HeapMemoryMaximum{clsLdr="18b4aac2",group="Kernal",} 14969470976
org_apache_ignite_ClusterLocalNode_HeapMemoryTotal{clsLdr="18b4aac2",group="Kernal",} 14969470976
org_apache_ignite_ClusterLocalNode_HeapMemoryUsed{clsLdr="18b4aac2",group="Kernal",} 95691472
org_apache_ignite_ClusterLocalNode_IdleTimePercentage{clsLdr="18b4aac2",group="Kernal",} 99.84683227539062
org_apache_ignite_ClusterLocalNode_LastDataVersion{clsLdr="18b4aac2",group="Kernal",} 1597000597696
org_apache_ignite_ClusterLocalNode_LastUpdateTime{clsLdr="18b4aac2",group="Kernal",} 1597002820085
org_apache_ignite_ClusterLocalNode_MaximumActiveJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_MaximumCancelledJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_MaximumJobExecuteTime{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_MaximumJobWaitTime{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_MaximumRejectedJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_MaximumThreadCount{clsLdr="18b4aac2",group="Kernal",} 81
org_apache_ignite_ClusterLocalNode_MaximumWaitingJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_NodeStartTime{clsLdr="18b4aac2",group="Kernal",} 1597000598048
org_apache_ignite_ClusterLocalNode_NonHeapMemoryCommitted{clsLdr="18b4aac2",group="Kernal",} 91750400
org_apache_ignite_ClusterLocalNode_NonHeapMemoryInitialized{clsLdr="18b4aac2",group="Kernal",} 2555904
org_apache_ignite_ClusterLocalNode_NonHeapMemoryMaximum{clsLdr="18b4aac2",group="Kernal",} -1
org_apache_ignite_ClusterLocalNode_NonHeapMemoryTotal{clsLdr="18b4aac2",group="Kernal",} -1
org_apache_ignite_ClusterLocalNode_NonHeapMemoryUsed{clsLdr="18b4aac2",group="Kernal",} 87494792
org_apache_ignite_ClusterLocalNode_OutboundMessagesQueueSize{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_ReceivedBytesCount{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_ReceivedMessagesCount{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_SentBytesCount{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_SentMessagesCount{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_StartTime{clsLdr="18b4aac2",group="Kernal",} 1597000595129
org_apache_ignite_ClusterLocalNode_TopologyVersion{clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_ClusterLocalNode_TotalBaselineNodes{clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_ClusterLocalNode_TotalBusyTime{clsLdr="18b4aac2",group="Kernal",} 3407
org_apache_ignite_ClusterLocalNode_TotalCancelledJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_TotalClientNodes{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_TotalCpus{clsLdr="18b4aac2",group="Kernal",} 6
org_apache_ignite_ClusterLocalNode_TotalExecutedJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_TotalExecutedTasks{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_TotalIdleTime{clsLdr="18b4aac2",group="Kernal",} 2221616
org_apache_ignite_ClusterLocalNode_TotalJobsExecutionTime{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_TotalNodes{clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_ClusterLocalNode_TotalRejectedJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_ClusterLocalNode_TotalServerNodes{clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_ClusterLocalNode_TotalStartedThreadCount{clsLdr="18b4aac2",group="Kernal",} 312
org_apache_ignite_ClusterLocalNode_UpTime{clsLdr="18b4aac2",group="Kernal",} 2225023
org_apache_ignite_Cluster_ActiveBaselineNodes{clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_Cluster_AverageActiveJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_AverageCancelledJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_AverageCpuLoad{clsLdr="18b4aac2",group="Kernal",} 0.03333333333333333
org_apache_ignite_Cluster_AverageJobExecuteTime{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_AverageJobWaitTime{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_AverageRejectedJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_AverageWaitingJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_BusyTimePercentage{clsLdr="18b4aac2",group="Kernal",} 0.15298724174499512
org_apache_ignite_Cluster_CurrentActiveJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_CurrentCancelledJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_CurrentCpuLoad{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_CurrentDaemonThreadCount{clsLdr="18b4aac2",group="Kernal",} 18
org_apache_ignite_Cluster_CurrentGcCpuLoad{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_CurrentIdleTime{clsLdr="18b4aac2",group="Kernal",} 2221616
org_apache_ignite_Cluster_CurrentJobExecuteTime{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_CurrentJobWaitTime{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_CurrentPmeDuration{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_CurrentRejectedJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_CurrentThreadCount{clsLdr="18b4aac2",group="Kernal",} 76
org_apache_ignite_Cluster_CurrentWaitingJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_HeapMemoryCommitted{clsLdr="18b4aac2",group="Kernal",} 1271398400
org_apache_ignite_Cluster_HeapMemoryInitialized{clsLdr="18b4aac2",group="Kernal",} 1052770304
org_apache_ignite_Cluster_HeapMemoryMaximum{clsLdr="18b4aac2",group="Kernal",} 14969470976
org_apache_ignite_Cluster_HeapMemoryTotal{clsLdr="18b4aac2",group="Kernal",} 14969470976
org_apache_ignite_Cluster_HeapMemoryUsed{clsLdr="18b4aac2",group="Kernal",} 95691472
org_apache_ignite_Cluster_IdleTimePercentage{clsLdr="18b4aac2",group="Kernal",} 99.84701538085938
org_apache_ignite_Cluster_LastDataVersion{clsLdr="18b4aac2",group="Kernal",} 1597000597696
org_apache_ignite_Cluster_LastUpdateTime{clsLdr="18b4aac2",group="Kernal",} 1597002820085
org_apache_ignite_Cluster_MaximumActiveJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_MaximumCancelledJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_MaximumJobExecuteTime{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_MaximumJobWaitTime{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_MaximumRejectedJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_MaximumThreadCount{clsLdr="18b4aac2",group="Kernal",} 76
org_apache_ignite_Cluster_MaximumWaitingJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_NodeStartTime{clsLdr="18b4aac2",group="Kernal",} 1597000598048
org_apache_ignite_Cluster_NonHeapMemoryCommitted{clsLdr="18b4aac2",group="Kernal",} 91750400
org_apache_ignite_Cluster_NonHeapMemoryInitialized{clsLdr="18b4aac2",group="Kernal",} 2555904
org_apache_ignite_Cluster_NonHeapMemoryMaximum{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_NonHeapMemoryTotal{clsLdr="18b4aac2",group="Kernal",} -1
org_apache_ignite_Cluster_NonHeapMemoryUsed{clsLdr="18b4aac2",group="Kernal",} 87494792
org_apache_ignite_Cluster_OutboundMessagesQueueSize{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_ReceivedBytesCount{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_ReceivedMessagesCount{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_SentBytesCount{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_SentMessagesCount{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_StartTime{clsLdr="18b4aac2",group="Kernal",} 1597000595129
org_apache_ignite_Cluster_TopologyVersion{clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_Cluster_TotalBaselineNodes{clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_Cluster_TotalBusyTime{clsLdr="18b4aac2",group="Kernal",} 3404
org_apache_ignite_Cluster_TotalCancelledJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_TotalClientNodes{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_TotalCpus{clsLdr="18b4aac2",group="Kernal",} 6
org_apache_ignite_Cluster_TotalExecutedJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_TotalExecutedTasks{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_TotalIdleTime{clsLdr="18b4aac2",group="Kernal",} 2221616
org_apache_ignite_Cluster_TotalJobsExecutionTime{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_TotalNodes{clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_Cluster_TotalRejectedJobs{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_Cluster_TotalServerNodes{clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_Cluster_TotalStartedThreadCount{clsLdr="18b4aac2",group="Kernal",} 312
org_apache_ignite_Cluster_UpTime{clsLdr="18b4aac2",group="Kernal",} 2225020
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
org_apache_ignite_DataRegion_OffHeapSize{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 268435456
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
org_apache_ignite_DataRegion_PhysicalMemoryPages{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 4102
org_apache_ignite_DataRegion_PhysicalMemoryPages{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 200
org_apache_ignite_DataRegion_PhysicalMemorySize{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_PhysicalMemorySize{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 16900240
org_apache_ignite_DataRegion_PhysicalMemorySize{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 824000
org_apache_ignite_DataRegion_TotalAllocatedPages{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_TotalAllocatedPages{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 4102
org_apache_ignite_DataRegion_TotalAllocatedPages{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 200
org_apache_ignite_DataRegion_TotalAllocatedSize{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_TotalAllocatedSize{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 16900240
org_apache_ignite_DataRegion_TotalAllocatedSize{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 824000
org_apache_ignite_DataRegion_TotalUsedPages{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_TotalUsedPages{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 4102
org_apache_ignite_DataRegion_TotalUsedPages{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 200
org_apache_ignite_DataRegion_UsedCheckpointBufferPages{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_UsedCheckpointBufferPages{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_UsedCheckpointBufferPages{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_UsedCheckpointBufferSize{name="TxLog",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_UsedCheckpointBufferSize{name="default",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataRegion_UsedCheckpointBufferSize{name="sysMemPlc",clsLdr="18b4aac2",group="DataRegionMetrics",} 0
org_apache_ignite_DataStorage_WalCompactionLevel{clsLdr="18b4aac2",group="DataStorage",} 1
org_apache_ignite_FailureHandling_CheckpointReadLockTimeout{clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_FailureHandling_LivenessCheckEnabled{clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_FailureHandling_SystemWorkerBlockedTimeout{clsLdr="18b4aac2",group="Kernal",} 10000
org_apache_ignite_IgniteKernal_LongJVMPausesCount{name="IgniteKernal",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_IgniteKernal_LongJVMPausesTotalDuration{name="IgniteKernal",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_IgniteKernal_NodeInBaseline{name="IgniteKernal",clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_IgniteKernal_PeerClassLoadingEnabled{name="IgniteKernal",clsLdr="18b4aac2",group="Kernal",} 0
org_apache_ignite_IgniteKernal_RebalanceEnabled{name="IgniteKernal",clsLdr="18b4aac2",group="Kernal",} 1
org_apache_ignite_IgniteKernal_StartTimestamp{name="IgniteKernal",clsLdr="18b4aac2",group="Kernal",} 1597000599749
org_apache_ignite_IgniteKernal_UpTime{name="IgniteKernal",clsLdr="18b4aac2",group="Kernal",} 2220326
org_apache_ignite_LocalDeploymentSpi_StartTimestamp{name="LocalDeploymentSpi",clsLdr="18b4aac2",group="SPIs",} 1597000597562
org_apache_ignite_LocalDeploymentSpi_UpTime{name="LocalDeploymentSpi",clsLdr="18b4aac2",group="SPIs",} 2222523
org_apache_ignite_RoundRobinLoadBalancingSpi_PerTask{name="RoundRobinLoadBalancingSpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_RoundRobinLoadBalancingSpi_StartTimestamp{name="RoundRobinLoadBalancingSpi",clsLdr="18b4aac2",group="SPIs",} 1597000597573
org_apache_ignite_RoundRobinLoadBalancingSpi_UpTime{name="RoundRobinLoadBalancingSpi",clsLdr="18b4aac2",group="SPIs",} 2222542
org_apache_ignite_SqlQuery_LongQueryTimeoutMultiplier{clsLdr="18b4aac2",group="SQL Query",} 2
org_apache_ignite_SqlQuery_LongQueryWarningTimeout{clsLdr="18b4aac2",group="SQL Query",} 3000
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
org_apache_ignite_TcpCommunicationSpi_StartTimestamp{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 1597000597529
org_apache_ignite_TcpCommunicationSpi_TcpNoDelay{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 1
org_apache_ignite_TcpCommunicationSpi_UnacknowledgedMessagesBufferSize{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpCommunicationSpi_UpTime{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 2222556
org_apache_ignite_TcpCommunicationSpi_UsePairedConnections{name="TcpCommunicationSpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpDiscoverySpi_AckTimeout{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 5000
org_apache_ignite_TcpDiscoverySpi_AvgMessageProcessingTime{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpDiscoverySpi_ClientMode{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpDiscoverySpi_ConnectionCheckInterval{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 500
org_apache_ignite_TcpDiscoverySpi_CoordinatorSinceTimestamp{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 1597000599301
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
org_apache_ignite_TcpDiscoverySpi_ProcessedMessages{name="TcpDiscoverySpi",key="TcpDiscoveryMetricsUpdateMessage",clsLdr="18b4aac2",group="SPIs",} 1109
org_apache_ignite_TcpDiscoverySpi_ProcessedMessages{name="TcpDiscoverySpi",key="TcpDiscoveryCustomEventMessage",clsLdr="18b4aac2",group="SPIs",} 3
org_apache_ignite_TcpDiscoverySpi_ProcessedMessages{name="TcpDiscoverySpi",key="TcpDiscoveryDiscardMessage",clsLdr="18b4aac2",group="SPIs",} 6
org_apache_ignite_TcpDiscoverySpi_ReconnectCount{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 10
org_apache_ignite_TcpDiscoverySpi_SocketTimeout{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 5000
org_apache_ignite_TcpDiscoverySpi_StartTimestamp{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpDiscoverySpi_StatisticsPrintFrequency{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 0
org_apache_ignite_TcpDiscoverySpi_ThreadPriority{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 10
org_apache_ignite_TcpDiscoverySpi_TotalProcessedMessages{name="TcpDiscoverySpi",clsLdr="18b4aac2",group="SPIs",} 1118
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
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridRestExecutor",clsLdr="18b4aac2",group="Thread Pools",} 4
org_apache_ignite_ThreadPool_CompletedTaskCount{name="GridSystemExecutor",clsLdr="18b4aac2",group="Thread Pools",} 244
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
org_apache_ignite_ThreadPool_LargestPoolSize{name="GridRestExecutor",clsLdr="18b4aac2",group="Thread Pools",} 4
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
org_apache_ignite_ThreadPool_TaskCount{name="GridRestExecutor",clsLdr="18b4aac2",group="Thread Pools",} 4
org_apache_ignite_ThreadPool_TaskCount{name="GridSystemExecutor",clsLdr="18b4aac2",group="Thread Pools",} 244
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
org_apache_ignite_Transaction_LockedKeysNumber{clsLdr="18b4aac2",group="TransactionMetrics",} 0
org_apache_ignite_Transaction_OwnerTransactionsNumber{clsLdr="18b4aac2",group="TransactionMetrics",} 0
org_apache_ignite_Transaction_TransactionsCommittedNumber{clsLdr="18b4aac2",group="TransactionMetrics",} 0
org_apache_ignite_Transaction_TransactionsHoldingLockNumber{clsLdr="18b4aac2",group="TransactionMetrics",} 0
org_apache_ignite_Transaction_TransactionsRolledBackNumber{clsLdr="18b4aac2",group="TransactionMetrics",} 0
org_apache_ignite_Transactions_LongTransactionTimeDumpThreshold{clsLdr="18b4aac2",group="Transactions",} 0
org_apache_ignite_Transactions_TransactionTimeDumpSamplesCoefficient{clsLdr="18b4aac2",group="Transactions",} 0
org_apache_ignite_Transactions_TransactionTimeDumpSamplesPerSecondLimit{clsLdr="18b4aac2",group="Transactions",} 5
org_apache_ignite_Transactions_TxOwnerDumpRequestsAllowed{clsLdr="18b4aac2",group="Transactions",} 1
org_apache_ignite_Transactions_TxTimeoutOnPartitionMapExchange{clsLdr="18b4aac2",group="Transactions",} 0
```
