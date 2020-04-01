示例的运行
===================================================

Apache Gossip 旨在作为其他人使用的运行库。也就是说，它仅想要成为能嵌入其他代码中的一个功能而已。

这些示例说明了一些从“轻”应用层调用 Gossip 的简单案例，用来说明本库的各种功能。

更多信息请参考：
* 这个[YouTube 视频](https://www.youtube.com/watch?v=bZXZrp7yBkw&t=39s)或 [BiliBili 视频](https://www.bilibili.com/video/BV1U741127K4)演示并阐明了第一个示例。
* 这个 [YouTube 视频](https://www.youtube.com/watch?v=SqkJs0QDRdk) 并阐明了第二个示例。
* 一个 [ Gossip 协议的综合性解释](https://en.wikipedia.org/wiki/Gossip_protocol)

初始化设置-前提条件
-----------------------------

这些说明假定您使用的是类 Unix 的命令行界面(若有必要请翻译)

在运行这些示例之前，您需要设置环境以运行 Java 和 Maven 命令：
* 安装 java 8 - [https://java.com/](https://java.com/), 和
* 安装 Maven - [https://maven.apache.org/install.html](https://maven.apache.org/install.html)


然后你将需要本地拷贝的代码。最简单的方式是将项目下载到本地文件夹：
* 打开网页 [https://github.com/apache/incubator-gossip](https://github.com/apache/incubator-gossip)
* 点击“ Clone of Download ”按钮
* 点击“ Download ZIP ”选项
* 将文件解压到合适的位置。接下来，我们将生成的项目文件夹命名为 **incubator-gossip**。


或者，您也可以 [clone](https://help.github.com/articles/cloning-a-repository/) GitHub 仓库。

最后，您将需要使用 Maven 来构建和安装必要的依赖项：
```
cd incubator-gossip
mvn install -DskipTests
```

在完成所有步骤后，您就可以开始运行第一个示例了……



运行 org.apache.gossip.examples.StandAloneNodeStandAloneExample
-------------------------------------------------
第一个示例说明了基础的、底层的通信层。通信层建立并维护了 gossip 网络集群。


在这个[ YouTube 视频](https://www.youtube.com/watch?v=bZXZrp7yBkw&t=39s) 中，描述了该架构并演示了此示例的运行。虽然使用的是较早版本系统来录制视频，但这些说明将继续带您运行该示例。

要从代码中运行该示例（在复制或下载的文件中），只需要将工作目录更改为 gossip-examples 模块，然后在 maven 中运行该程序。

具体来说，在复制或下载仓库后可以：
```
cd incubator-gossip/gossip-examples
mvn exec:java -Dexec.mainClass=org.apache.gossip.examples.StandAloneNodeStandAloneExample -Dexec.args="udp://localhost:10000 0 udp://localhost:10000 0"
```


这将建立一个 StandAloneNode ，开始监听它自身。它的参数是：
1. 节点的URI（主机和端口）- **udp://localhost:10000**
2. 节点的 ID - **0**
3. “种子”节点的 URI -  **udp://localhost:10000**
4. 该种子节点的ID- **0**


注意:要停止该示例，只需杀死进程（如 Ctrl C）。

在此程序中，输出使用一种“终端转义序列”，该序列清除终端显示并将光标重置到左上角。如果由于某种原因在您的情况下不起作用，则可以在命令行的 args 中添加（optional）标志“-s”，以消除这种“清除屏幕”行为。这是：
```
cd incubator-gossip/gossip-examples
mvn exec:java -Dexec.mainClass=org.apache.gossip.examples.StandAloneNodeStandAloneExample -Dexec.args="-s udp://localhost:10000 0 udp://localhost:10000 0"
```

通常情况下，您会在分散主机的网络上建立节点，但在这里，为了说明 Gossip 中的基本通信，我们仅在同一主机 localhost 上运行所有节点。种子节点通常将是网络中其他节点之一：该节点有足够的信息来（最终）获取其集群中所有节点的列表。

您将会看到这个 gossip 节点打印出两个其他已知节点的列表 List ，即活动节点和死亡节点。活动节点假定为活动且已连接的节点，失效节点是“丢失”了足够长的时间而被假定为休眠或断开连接的节点。可在视频中查看详细信息。

在这个集群仅运行一个节点的情况下，没有检测到其他节点，因此活动列表和无效列表都是空的。


然后，在分割开来的单独的终端窗口中，使用 cd 转到相同的文件夹，并输入相同的运行命令，但改变前两个参数来印证这是一个不同的节点
1. 节点的主机/端口- **udp://localhost:10001**
2. 节点的ID- **1**

像这样：
```
cd incubator-gossip/gossip-examples
mvn exec:java -Dexec.mainClass=org.apache.gossip.examples.StandAloneNodeStandAloneExample -Dexec.args="udp://localhost:10001 1 udp://localhost:10000 0"
```
现在，因为“种子节点”是我们启动的第一个节点，因此第二个节点在监听第一个节点，并且它们交换已知节点的列表。然后开始互相监听，以便在短期内它们都拥有一个实时注释的列表，其中一个成员是集群中的另一个实时节点。

最后，在另一个终端窗口中，cd 到同一文件夹并输入相同的运行命令，其中前两个参数改为
1. 节点的主机/端口 - **udp://localhost:10002**
2. 节点的 ID - **2**

```
cd incubator-gossip/gossip-examples
mvn exec:java -Dexec.mainClass=org.apache.gossip.examples.StandAloneNodeStandAloneExample -Dexec.args="udp://localhost:10002 2 udp://localhost:10000 0”
```
现在，集群的活动节点列表会聚合起来，反映了每个节点与其他两个节点的连接。

为看到一个节点被移到了死亡列表。在正在运行的终端中的一个终止该进程：在终端窗口中输入 Ctrl C。然后，剩下的两个节点的活动-死亡列表将收敛，显示有一个节点处于活动状态，而有一个节点处于死亡状态。再次启动刚刚“丢失”的节点，它将再次出现在活动列表中。请注意，哪个节点处于休眠状态都无关紧要，每个活动节点（最终）都将与集群中的其他活动节点进行通信并获取必要的更新状态信息。

如果阅读该代码，您将看到它定义了一个（名称 = 'mycluster'）和许多其他设置的详细信息。请参阅视频，以获取关于设置的更完整描述以及有关节点之间交互的更详细描述。

还要注意，运行这些节点的进程会生成一组记录节点状态的 JSON 文件（位于节点运行的 “base” 目录中）。当故障节点的恢复，这可以启用快速启动。在此示例中，要使用control-c恢复已杀死的节点，只需重新发出命令以运行该节点。

运行 org.apache.gossip.examples.StandAloneNodeCrdtOrSetStandAloneExample
----------------------------------------------------------

第二个示例说明了如何使用数据层共享结构化信息：一组字符串的共享和共享计数器的表示形式。表示这些共享值的对象是免冲突的可复制的数据类型（即CRDT）的特例。
 
 这是这些CRDT对象要解决的问题：由于集群中的每个节点都可以用任意顺序向集群中的任何其他节点发送消息，因此反映数据结构内容的消息可以按任何顺序到达。
 例如，一个节点可能会将一个对象添加到集合中，然后将修改后的集合发送到另一个删除了该对象，并将这个消息发送到第三各节点。如果第一个节点也向第三个节点发送了消息（对象在集合中），则第三个节点可能会收到存在冲突的信息。但是，CRDT数据结构始终包含足够的信息来解决冲突。


和第一个演示一样，还有一个YouTube视频，该视频说明了这个问题并展示了如何解决它，并解释了运行过程。
示例: [https://www.youtube.com/watch?v=SqkJs0QDRdk](https://www.youtube.com/watch?v=SqkJs0QDRdk) .


同样，我们将运行该这个示例的三个实例，来说明集群中节点的操作。该应用程序的参数与第一个示例中的参数相同。此示例中的区别在于，每个节点将读取更改其本地状态信息的“命令（commands）”。我们将会在稍后说明。但是首先，我们让这三个节点运行：



在第一个终端窗口中：
```
cd incubator-gossip/gossip-examples
mvn exec:java -Dexec.mainClass=org.apache.gossip.examples.StandAloneNodeCrdtOrSetStandAloneExample -Dexec.args="udp://localhost:10000 0 udp://localhost:10000 0"
```

在第二个终端窗口中：
```
cd incubator-gossip/gossip-examples
mvn exec:java -Dexec.mainClass=org.apache.gossip.examples.StandAloneNodeCrdtOrSetStandAloneExample -Dexec.args="udp://localhost:10001 1 udp://localhost:10000 0"
```

在第三个终端窗口中：
```
cd incubator-gossip/gossip-examples
mvn exec:java -Dexec.mainClass=org.apache.gossip.examples.StandAloneNodeCrdtOrSetStandAloneExample -Dexec.args="udp://localhost:10002 2 udp://localhost:10000 0"
```


现在，在任意一个终端窗口中，你都可以从以下的命令集中键入一个命令来更改本地存储的数据。注意，在你键入时，终端会继续同时输出字符，这要求你必须盲打键盘，但是当你按回车键来结束输入行时，输入将短暂显示，然后滚动到屏幕之外。

当你键入这些命令中的其中一个时，便可以观察这个数据在集群中的传播。这些命令是：
```
a string
r string
g number
```

* **a** 是“添加”命令；它将字符串添加到共享集合中 - 最终，您应该能在所有节点上看到这个集合中的新增值。
* **r** 是“删除”命令；它将字符串从集合中删除（如果它存在于集合中）- 最终，您应该能看到这个值将离开所有节点上的共享集合
* **g** 是“全局增量”命令；假设它的参数是一个数字，然后将该数字加到一个累加器上。最终，所有节点上的累加器将稳定为相同的值。

这些值的 CRDT 表示可确保不论从集群中其他节点的到达的信息顺序如何，所有节点值的结果都最终达到到相同状态，

作视频的一个补充，这个[维基百科文章](https://en.wikipedia.org/wiki/Conflict-free_replicated_data_type)描述了各种CRDT的表示形式及他们的作用，以及关于一些有趣的应用程序的信息。


运行 org.apache.gossip.examples.StandAloneDatacenterAndRackStandAloneExample
--------------------------------------------------------------


最后一个示例说明了对集群中节点的预期“响应性”更加细粒度的访问控制。Apache gossip 被设计为旨在，利用 gossip 协议进行点对点通信，能嵌入应用程序中的库。
 
 The effectiveness of communications among nodes in a gossip cluster can be tuned to the expected latency of message transmission and expected responsiveness of other nodes in the network. 
可以将 gossip 群集中节点之间的通信效率调整为消息传输的预期延迟和网络中其他节点的预期响应能力。

这个示例说明了这种控制类型的一个模型：一种叫做“数据中心和机架”的节点分布模型。在此模型中，假设位于相同“数据中心”（也许位于不同“机架”中）的节点具有非常低的延迟和较高的通信保真度。然而，位于不同数据中心中的节点假设需要更多的时间进行通信，并且通信失败率更高，因此可以调整通信以容忍延迟和成功传输上有更多变化，但这会造成需要更长的时间达到“稳定”。

因此，此示例中的应用程序有额外的几个参数，一个数据中心ID和一个机架ID。

要启动第一个节点（在第一个终端窗口中），请键入以下内容：

```
cd incubator-gossip/gossip-examples
mvn exec:java -Dexec.mainClass=org.apache.gossip.examples.StandAloneDatacenterAndRackStandAloneExample -Dexec.args="udp://localhost:10000 0 udp://localhost:10000 0 1 2"
```
前四个参数与其他两个示例相同，后面两个参数是新参数：
1. 节点的URI（主机和端口）- **udp://localhost:10000**
2. 节点的ID - **0**
3. “种子”节点的URI - **udp://localhost:10000**
4. 该种子节点的ID - **0**
5. 数据中心ID - **1**
6. 机架ID - **2**

然后，让我们设置另外的两个节点（每个节点在一个单独的终端窗口中），一个在不同机架上的同一数据中心，另一个在不同的数据中心中。
```
cd incubator-gossip/gossip-examples
mvn exec:java -Dexec.mainClass=org.apache.gossip.examples.StandAloneDatacenterAndRackStandAloneExample -Dexec.args="udp://localhost:10001 1 udp://localhost:10000 0 1 3"
```

```
cd incubator-gossip/gossip-examples
mvn exec:java -Dexec.mainClass=org.apache.gossip.examples.StandAloneDatacenterAndRackStandAloneExample -Dexec.args="udp://localhost:10002 2 udp://localhost:10000 0 2 2"
```
现在，运行在第一个终端窗口中的应用程序，被识别为在数据中心1和机架2中运行；第二个终端窗口中的应用程序在同一数据中心的不同机架（数据中心1，机架3）中运行。同时，第三个终端中的应用程序在不同的数据中心（数据中心2）中运行。

如果将第一个终端窗口中的节点终止（Ctrl C），你会观察到第三个终端窗口中的进程比起第二个窗口需要更长的时间才能稳定到正确的状态，因为它预期消息在传输中将会有更高的延迟，（因此）它更能容忍消息中的延迟（和丢弃），从而花费更长的时间来检测被终止的进程是“离线”的。

注意事项
-----------
示例运行的描述到此结束。

该项目是一个 Apache [孵化器项目](http://incubator.apache.org/projects/gossip.html). 
在[官方网站](http://gossip.incubator.apache.org/community/)上有更多信息：见尤其是“community”栏目下的“get involved”。请尽情享受，如果您发现此库有帮助请用任意方式告诉我们。



