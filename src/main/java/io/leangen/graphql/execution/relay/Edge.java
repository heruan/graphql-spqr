package io.leangen.graphql.execution.relay;

/**
 * Created by bojan.tomic on 4/6/16.
 */
public interface Edge<N> {

    String getCursor();

    N getNode();
}
