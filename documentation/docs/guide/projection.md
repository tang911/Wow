# 投影处理器

*投影处理器*是一种将领域模型的状态以符合查询需求的形式进行组织和存储的机制。

由于 _EventStore_ 中存储的是聚合根领域事件流，对于查询非常不友好。
通过结合 _CQRS_ 架构模式，将*读模型*和*写模型*分开，允许针对查询的需求进行专门的优化。

在 _Wow_ 框架中，*读模型投影*是通过定义*投影处理器*实现的。投影处理器负责处理领域事件，更新读模型的索引状态，以反映最新的数据状态。

:::tip
需要特别指出的是，当快照模式设置为 `all` 时，投影就不是必须的。

在一般场景中，聚合根的最新状态快照可以当做读模型，比如 [事件补偿控制台](./event-compensation) 就没有定义投影处理器，直接使用了最新状态快照作为读模型。
:::

- 投影处理器需要标记 `@ProjectionProcessorComponent` 注解，以便框架能够自动发现。
- 领域事件处理函数需要添加 `@OnEvent` 注解，但该注解不是必须的，默认情况下命名为 `onEvent` 即表明该函数为领域事件处理函数。
- 领域事件处理函数接受的参数为：具体领域事件 (`OrderCreated`)、领域事件 (`DomainEvent<OrderCreated>`)。

```kotlin
@ProjectionProcessorComponent
class OrderProjector {

    fun onEvent(orderCreated: OrderCreated) {
        // 根据领域事件更新读模型
    }
}
```