# Gossip ![Build status](https://travis-ci.org/edwardcapriolo/incubator-gossip.svg?)

 Gossip协议 是一种用于一组节点发现和检查集群活性的方法。更多信息请访问 http://en.wikipedia.org/wiki/Gossip_protocol.

原始的实现fork自 https://code.google.com/p/java-gossip/。一些对错误的修复和更改已经加入。

gossip-examples 模块中提供了一组简易运行的示例，这些示例说明了 Gossip 的各种功能。该模块中的 README.md 文件描述了如何运行这些示例。

下面的一系列代码片段，显示了如何将 Apache Gossip 集成到你的项目中。

使用方法
-----
你需要一个或多个种子节点( Seed Node )去实现 gossip。种子只是初始连接位置的一个列表。

```java
  GossipSettings settings = new GossipSettings();
  int seedNodes = 3;
  List<GossipMember> startupMembers = new ArrayList<>();
  for (int i = 1; i < seedNodes+1; ++i) {
    URI uri = new URI("udp://" + "127.0.0.1" + ":" + (50000 + i));
    startupMembers.add(new RemoteGossipMember(cluster, uri, i + ""));
  }
```

我们在这里开始 5 个 gossip 进程 ，并检查它们是否能相互发现。（通常它们应该位于不同的主机上，但在这里，我们只给每个进程提供一个不同的本地ip地址）

```java
  List<GossipService> clients = new ArrayList<>();
  int clusterMembers = 5;
  for (int i = 1; i < clusterMembers+1; ++i) {
    URI uri = new URI("udp://" + "127.0.0.1" + ":" + (50000 + i));
   GossipService gossipService = new GossipService(cluster, uri, i + "",
             startupMembers, settings, (a,b) -> {});
  }
```

之后我们可以检查节点是否能够互相发现。


```java
  Thread.sleep(10000);
  for (int i = 0; i < clusterMembers; ++i) {
    Assert.assertEquals(4, clients.get(i).getGossipManager().getLiveMembers().size());
  }
```

Settings 文件的用法
-----

对于一个非常简单的客户端的设置文件，首先你需要像这样的一个JSON文件：

```json
[{
  "cluster":"9f1e6ddf-8e1c-4026-8fc6-8585d0132f77",
  "id":"447c5bec-f112-492d-968b-f64c8e36dfd7",
  "uri":"udp://127.0.0.1:50001",
  "gossip_interval":1000,
  "cleanup_interval":10000,
  "members":[
    {"cluster": "9f1e6ddf-8e1c-4026-8fc6-8585d0132f77","uri":"udp://127.0.0.1:5000"}
  ]
}]
```

对照：

* `cluster`  - 集群的名称
* `id` - 此节点的唯一 ID（你可以使用任何字符串，但在上面我们使用UUID）
* `uri`  - 一个 URI 对象，其中包含要在节点计算机上的默认适配器上使用的IP /主机号和端口号
* `gossip_interval` - gossip 成员列表到其他节点的频率（以毫秒为单位）
* `cleanup_interval` - 何时删除“死亡”节点（以毫秒为单位）（已弃用的可能会回来）
* `members` - 初始种子节点

然后可以非常简单的启动本地节点：

```java
GossipService gossipService = new GossipService(
  StartupSettings.fromJSONFile( "node_settings.json" )
);
gossipService.start();
```

在完成所有操作后，使用以下命令关闭：

```java
gossipService.shutdown();
```

事件监听器（ Event Listener ）
------

可使用返回不可变列表 List<> 的 getter 方法来查询状态。


```java
   public List<LocalGossipMember> getLiveMembers()
   public List<LocalGossipMember> getDeadMembers()
```

这些内容可以从 `GossipServicel类` 里的 `GossipManager方法` 访问，例如：  
`gossipService.getGossipManager().getLiveMembers();`


你也自己可以绑定一个事件监听器：

```java
    GossipService gossipService = new GossipService(cluster, uri, i + "", startupMembers,
    settings, new GossipListener() {
      @Override
      public void gossipEvent(GossipMember member, GossipState state) {
        System.out.println(System.currentTimeMillis() + " Member " + j + " reports "
                + member + " " + state);
      }
  });
  //lambda 表达式的语法是 (a,b) -> { }  //完美！

```

