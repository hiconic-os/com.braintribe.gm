# graph-fetching

A framework for efficient deep fetching of entity graphs from a `PersistenceGmSession` (and detached mode), allowing control over which object graph branches are loaded from persistence.

## Features
- Declarative, graph-like specification of properties/entities to fetch (like GraphQL, but model-driven)
- Type-safe (prototype-based) and dynamic (node-tree) fetch plan specification
- Handles recursive, to-one and to-many (collection) relationships efficiently
- Extensible, with clear separation between fetch plan, task scheduling, and query execution

## Usage

### 1. Type-safe Prototype Fetching
Prepare a prototype, mark all desired fetch properties via getter/setter calls, and use it as fetch plan root:

```java
// Mark what you want through regular getter invocations on the prototype
MyEntityType proto = Fetching.selectPrototype(MyEntityType.T);
proto.getRelatedEntity().getSubProperty();
proto.getCollectionOfFoos().iterator();

// Use prototype as graph root
EntityGraphNode fetchGraph = Fetching.rootNode(proto);
Fetching.fetch(session, fetchGraph, entities);
```

### 2. Dynamic/Untyped Node Fetching
For programmatic scenarios or UI-driven fetches:

```java
EntityGraphNode fetchGraph = Fetching.rootNode(MyEntityType.T,
    Fetching.node("relatedEntity",
        Fetching.node("subProperty")
    ),
    Fetching.node("collectionOfFoos")
);
Fetching.fetch(session, fetchGraph, entities);
```

### 3. Detached Fetch (for transfer/read-only use)
```java
List<GenericEntity> detached = Fetching.fetchDetached(session, fetchGraph, entities);
```

## Architecture At a Glance
- **Fetching Interface**: Entry-point API for all user interactions
- **GraphNode Hierarchy**: Describes the shape of the object graph to fetch
- **FetchProcessing**: Schedules and processes fetch tasks by decomposing the graph, using efficient batch queries
- **ToOneRecursiveFetching / ToManyFetching**: Internal implementation for to-one and to-many (collection) property graph walking and query wiring

## Extensibility
- Plug in custom `EntityGraphNode` or `FetchingTools` for advanced graph transformation or clone/detachment logic
- Model reflection (properties, types) abstracted for integration with generic/gm-based persistence

## Similar To
- GraphQL (but for POJO/entity model APIs, not JSON over HTTP)
- ORM fetch graphs (but safer, more explicit and introspectable)

----

For more details, consult the Javadoc on the APIs or the code comments in the implementation.
