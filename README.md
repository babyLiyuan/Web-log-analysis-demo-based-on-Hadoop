# Web-log-analysis-demo-based-on-Hadoop
The demo can be divided into four parts: log collection, log clean-up and log analysis.

## 简介

Demo利用了Hadoop，Hive，Flume等工具，实现了简单的日志收集、日志清理、日志分析功能，以达到入侵检测的目的。

## 环境

###软件版本

- Ubuntu 14.04

- Hadoop 2.6.0

- Flume 1.5.0

- Hive 0.13.1

###Flume的配置

- 分布式集群中每一台机器上的配置

```bash
# clientAgent
clientAgent.sources = s1
clientAgent.sinks = k1
clientAgent.channels = c1

# sources
clientAgent.sources.s1.type = spooldir
clientAgent.sources.s1.spoolDir =/home/hadoop/aboutflume/log
clientAgent.sources.s1. decodeErrorPolicy = IGNORE
clientAgent.sources.s1.batchSize = 1000
clientAgent.channels.c1.deserializer.maxLineLength=1048576

# sinks
clientAgent.sinks.k1.type = avro
clientAgent.sinks.k1.hostname = ubuntu2
clientAgent.sinks.k1.port = 41415 
clientAgent.sinks.k1.hdfs. rollSize = 0
clientAgent.sinks.k1.hdfs. rollCount = 0
clientAgent.sinks.k1.hdfs. rollInterval = 300

# channels
clientAgent.channels.c1.type = file
clientAgent.channels.c1.checkpointDir=/home/hadoop/aboutflume/checkpoint
clientAgent.channels.c1.dataDirs=/home/hadoop/aboutflume/data
clientAgent.channels.c1. capacity = 200000000
clientAgent.channels.c1.transactionCapacity = 100000
clientAgent.channels.c1.keep-alive = 30
clientAgent.channels.c1. write-timeout = 30
clientAgent.channels.c1. checkpoint-timeout = 600

# link
clientAgent.sources.s1.channels = c1
clientAgent.sinks.k1.channel = c1
```

- 日志收集器的配置

```bash
# collectorAgent
collectorAgent.sources  = s2
collectorAgent.sinks = k2
collectorAgent.channels = c2

# sources
collectorAgent.sources.s2.type = avro
collectorAgent.sources.s2.bind = ubuntu2
collectorAgent.sources.s2.port = 41415

# sinks
collectorAgent.sinks.k2.type = hdfs
collectorAgent.sinks.k2.hdfs.path=hdfs://10.211.55.5:8020/aboutflume/log/%y-%m-%d/%H
collectorAgent.sinks.k2.hdfs.filePrefix = events-%{file}
collectorAgent.sinks.k2.hdfs.fileType = DataStream
collectorAgent.sinks.k2.hdfs. writeFormat = Text
collectorAgent.sinks.k2.hdfs. batchSize = 1000
collectorAgent.sinks.k2.hdfs.round = true
collectorAgent.sinks.k2.hdfs.roundValue = 1
collectorAgent.sinks.k2.hdfs.roundUnit = hour
collectorAgent.sinks.k2.hdfs.useLocalTimeStamp = true
collectorAgent.sinks.k2.hdfs. rollSize = 0
collectorAgent.sinks.k2.hdfs. rollCount = 0
collectorAgent.sinks.k2.hdfs. rollInterval = 300
collectorAgent.sinks.k2.hdfs.minBlockReplicas=1

# channels
collectorAgent.channels.c2.type = file
collectorAgent.channels.c2.checkpointDir=/home/hadoop/aboutflume/checkpoint collectorAgent.channels.c2.dataDirs=/home/hadoop/aboutflume/data
collectorAgent.channels.c2. capacity = 200000000
collectorAgent.channels.c2. transactionCapacity = 100000
collectorAgent.channels.c2. checkpointInterval = 60000

# link
collectorAgent.sources.s2.channels = c2
collectorAgent.sinks.k2.channel = c2
```





