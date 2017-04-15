package io.leangen.graphql.generator.mapping.common;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import graphql.Scalars;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;
import io.leangen.graphql.annotations.RelayId;
import io.leangen.graphql.execution.ResolutionEnvironment;
import io.leangen.graphql.generator.BuildContext;
import io.leangen.graphql.generator.OperationMapper;
import io.leangen.graphql.generator.mapping.ArgumentInjector;
import io.leangen.graphql.generator.mapping.OutputConverter;

/**
 * @author Bojan Tomic (kaqqao)
 */
public class RelayIdAdapter extends AbstractionCollectingMapper implements ArgumentInjector, OutputConverter {

    @Override
    public GraphQLOutputType graphQLType(AnnotatedType javaType, Set<Type> abstractTypes, OperationMapper operationMapper, BuildContext buildContext) {
        return Scalars.GraphQLID;
    }

    @Override
    public GraphQLInputType graphQLInputType(AnnotatedType javaType, Set<Type> abstractTypes, OperationMapper operationMapper, BuildContext buildContext) {
        return Scalars.GraphQLID;
    }

    @Override
    protected void registerAbstract(AnnotatedType type, Set<Type> abstractTypes, BuildContext buildContext) {
        abstractTypes.addAll(collectAbstract(type, new HashSet<>(), buildContext));
    }
    
    @Override
    public Object convertOutput(Object original, AnnotatedType type, ResolutionEnvironment resolutionEnvironment) {
        return resolutionEnvironment.globalEnvironment.relay.toGlobalId(resolutionEnvironment.parentType.getName(), resolutionEnvironment.valueMapper.toString(original));
    }

    @Override
    public Object getArgumentValue(Object input, AnnotatedType type, ResolutionEnvironment resolutionEnvironment) {
        if (input == null) {
            return null;
        }
        String rawId = input.toString();
        String id = rawId;
        try {
            id = resolutionEnvironment.globalEnvironment.relay.fromGlobalId(rawId).getId();
        } catch (Exception e) {/*no-op*/}
        return resolutionEnvironment.valueMapper.fromString(id, type);
    }

    @Override
    public boolean supports(AnnotatedType type) {
        return type.isAnnotationPresent(RelayId.class);
    }
}
