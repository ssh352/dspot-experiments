/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.contentpacks.facades;


import Converter.Type.LOOKUP_TABLE;
import LookupTableService.Builder;
import LookupTableService.Function;
import MessageInput.FIELD_CONFIGURATION;
import MessageInput.FIELD_TITLE;
import MessageInput.FIELD_TYPE;
import MessageInput.Factory;
import ModelTypes.GROK_PATTERN_V1;
import ModelTypes.INPUT_V1;
import ModelTypes.LOOKUP_TABLE_V1;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.graph.Graph;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.InMemoryMongoDb;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.collections.map.HashedMap;
import org.graylog2.contentpacks.EntityDescriptorIds;
import org.graylog2.contentpacks.model.ModelId;
import org.graylog2.contentpacks.model.entities.ConverterEntity;
import org.graylog2.contentpacks.model.entities.Entity;
import org.graylog2.contentpacks.model.entities.EntityDescriptor;
import org.graylog2.contentpacks.model.entities.EntityExcerpt;
import org.graylog2.contentpacks.model.entities.EntityV1;
import org.graylog2.contentpacks.model.entities.ExtractorEntity;
import org.graylog2.contentpacks.model.entities.GrokPatternEntity;
import org.graylog2.contentpacks.model.entities.InputEntity;
import org.graylog2.contentpacks.model.entities.LookupTableEntity;
import org.graylog2.contentpacks.model.entities.NativeEntity;
import org.graylog2.contentpacks.model.entities.references.ReferenceMapUtils;
import org.graylog2.contentpacks.model.entities.references.ValueReference;
import org.graylog2.database.MongoConnectionRule;
import org.graylog2.database.NotFoundException;
import org.graylog2.inputs.Input;
import org.graylog2.inputs.InputImpl;
import org.graylog2.inputs.InputService;
import org.graylog2.inputs.extractors.GrokExtractor;
import org.graylog2.inputs.extractors.LookupTableExtractor;
import org.graylog2.lookup.LookupTableService;
import org.graylog2.lookup.db.DBLookupTableService;
import org.graylog2.lookup.dto.LookupTableDto;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.inputs.Extractor;
import org.graylog2.plugin.inputs.MessageInput;
import org.graylog2.shared.bindings.providers.ObjectMapperProvider;
import org.graylog2.shared.inputs.MessageInputFactory;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class InputFacadeTest {
    @ClassRule
    public static final InMemoryMongoDb IN_MEMORY_MONGO_DB = newInMemoryMongoDbRule().build();

    @Rule
    public final MongoConnectionRule mongoRule = MongoConnectionRule.build("test");

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private final ObjectMapper objectMapper = new ObjectMapperProvider().get();

    @Mock
    private LookupTableService lookupTableService;

    @Mock
    private Builder lookupuptableBuilder;

    @Mock
    private Function lookupTable;

    @Mock
    private DBLookupTableService dbLookupTableService;

    @Mock
    private LookupTableDto lookupTableWhois;

    @Mock
    private LookupTableDto lookupTableTor;

    @Mock
    private MessageInputFactory messageInputFactory;

    @Mock
    private ServerStatus serverStatus;

    private InputService inputService;

    private InputFacade facade;

    private Set<PluginMetaData> pluginMetaData;

    private Map<String, Factory<? extends MessageInput>> inputFactories;

    @Test
    public void exportNativeEntity() {
        final ImmutableMap<String, Object> fields = ImmutableMap.of(FIELD_TITLE, "Input Title", FIELD_TYPE, "org.graylog2.inputs.raw.udp.RawUDPInput", FIELD_CONFIGURATION, Collections.emptyMap());
        final InputImpl input = new InputImpl(fields);
        final ImmutableList<Extractor> extractors = ImmutableList.of();
        final InputWithExtractors inputWithExtractors = InputWithExtractors.create(input, extractors);
        final EntityDescriptor descriptor = EntityDescriptor.create(input.getId(), INPUT_V1);
        final EntityDescriptorIds entityDescriptorIds = EntityDescriptorIds.of(descriptor);
        final Entity entity = facade.exportNativeEntity(inputWithExtractors, entityDescriptorIds);
        assertThat(entity).isInstanceOf(EntityV1.class);
        assertThat(entity.id()).isEqualTo(ModelId.of(entityDescriptorIds.get(descriptor).orElse(null)));
        assertThat(entity.type()).isEqualTo(INPUT_V1);
        final EntityV1 entityV1 = ((EntityV1) (entity));
        final InputEntity inputEntity = objectMapper.convertValue(entityV1.data(), InputEntity.class);
        assertThat(inputEntity.title()).isEqualTo(ValueReference.of("Input Title"));
        assertThat(inputEntity.type()).isEqualTo(ValueReference.of("org.graylog2.inputs.raw.udp.RawUDPInput"));
        assertThat(inputEntity.configuration()).isEmpty();
    }

    @Test
    @UsingDataSet(locations = "/org/graylog2/contentpacks/inputs.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void exportEntity() {
        final ModelId id = ModelId.of("5acc84f84b900a4ff290d9a7");
        final EntityDescriptor descriptor = EntityDescriptor.create(id, INPUT_V1);
        final EntityDescriptorIds entityDescriptorIds = EntityDescriptorIds.of(descriptor);
        final Entity entity = facade.exportEntity(descriptor, entityDescriptorIds).orElseThrow(AssertionError::new);
        assertThat(entity).isInstanceOf(EntityV1.class);
        assertThat(entity.id()).isEqualTo(ModelId.of(entityDescriptorIds.get(descriptor).orElse(null)));
        assertThat(entity.type()).isEqualTo(INPUT_V1);
        final EntityV1 entityV1 = ((EntityV1) (entity));
        final InputEntity inputEntity = objectMapper.convertValue(entityV1.data(), InputEntity.class);
        assertThat(inputEntity.title()).isEqualTo(ValueReference.of("Local Raw UDP"));
        assertThat(inputEntity.type()).isEqualTo(ValueReference.of("org.graylog2.inputs.raw.udp.RawUDPInput"));
        assertThat(inputEntity.global()).isEqualTo(ValueReference.of(false));
        assertThat(inputEntity.configuration()).containsEntry("bind_address", ValueReference.of("127.0.0.1")).containsEntry("port", ValueReference.of(5555));
    }

    @Test
    public void createExcerpt() {
        final ImmutableMap<String, Object> fields = ImmutableMap.of(DashboardImpl.FIELD_TITLE, "Dashboard Title");
        final InputImpl input = new InputImpl(fields);
        final InputWithExtractors inputWithExtractors = InputWithExtractors.create(input);
        final EntityExcerpt excerpt = facade.createExcerpt(inputWithExtractors);
        assertThat(excerpt.id()).isEqualTo(ModelId.of(input.getId()));
        assertThat(excerpt.type()).isEqualTo(INPUT_V1);
        assertThat(excerpt.title()).isEqualTo(input.getTitle());
    }

    @Test
    @UsingDataSet(locations = "/org/graylog2/contentpacks/inputs.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void listEntityExcerpts() {
        final EntityExcerpt expectedEntityExcerpt1 = EntityExcerpt.builder().id(ModelId.of("5adf25294b900a0fdb4e5365")).type(INPUT_V1).title("Global Random HTTP").build();
        final EntityExcerpt expectedEntityExcerpt2 = EntityExcerpt.builder().id(ModelId.of("5acc84f84b900a4ff290d9a7")).type(INPUT_V1).title("Local Raw UDP").build();
        final EntityExcerpt expectedEntityExcerpt3 = EntityExcerpt.builder().id(ModelId.of("5ae2eb0a3d27464477f0fd8b")).type(INPUT_V1).title("TEST PLAIN TEXT").build();
        final EntityExcerpt expectedEntityExcerpt4 = EntityExcerpt.builder().id(ModelId.of("5ae2ebbeef27464477f0fd8b")).type(INPUT_V1).title("TEST PLAIN TEXT").build();
        final Set<EntityExcerpt> entityExcerpts = facade.listEntityExcerpts();
        assertThat(entityExcerpts).containsOnly(expectedEntityExcerpt1, expectedEntityExcerpt2, expectedEntityExcerpt3, expectedEntityExcerpt4);
    }

    @Test
    @UsingDataSet(locations = "/org/graylog2/contentpacks/inputs.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void collectEntity() {
        final EntityDescriptor descriptor = EntityDescriptor.create("5adf25294b900a0fdb4e5365", INPUT_V1);
        final EntityDescriptorIds entityDescriptorIds = EntityDescriptorIds.of(descriptor);
        final Optional<Entity> collectedEntity = facade.exportEntity(descriptor, entityDescriptorIds);
        assertThat(collectedEntity).isPresent().containsInstanceOf(EntityV1.class);
        final EntityV1 entity = ((EntityV1) (collectedEntity.get()));
        assertThat(entity.id()).isEqualTo(ModelId.of(entityDescriptorIds.get(descriptor).orElse(null)));
        assertThat(entity.type()).isEqualTo(INPUT_V1);
        final InputEntity inputEntity = objectMapper.convertValue(entity.data(), InputEntity.class);
        assertThat(inputEntity.title()).isEqualTo(ValueReference.of("Global Random HTTP"));
        assertThat(inputEntity.type()).isEqualTo(ValueReference.of("org.graylog2.inputs.random.FakeHttpMessageInput"));
        assertThat(inputEntity.global()).isEqualTo(ValueReference.of(true));
        assertThat(inputEntity.staticFields()).containsEntry("custom_field", ValueReference.of("foobar"));
        assertThat(inputEntity.configuration()).isNotEmpty();
        assertThat(inputEntity.extractors()).hasSize(5);
    }

    @Test
    @UsingDataSet(locations = "/org/graylog2/contentpacks/inputs.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void findExisting() {
        final Map<String, Object> configuration = new HashMap<>();
        configuration.put("override_source", null);
        configuration.put("recv_buffer_size", 262144);
        configuration.put("bind_address", "127.0.0.1");
        configuration.put("port", 5555);
        configuration.put("number_worker_threads", 8);
        final Entity entity = EntityV1.builder().id(ModelId.of("5acc84f84b900a4ff290d9a7")).type(INPUT_V1).data(objectMapper.convertValue(InputEntity.create(ValueReference.of("Local Raw UDP"), ReferenceMapUtils.toReferenceMap(configuration), Collections.emptyMap(), ValueReference.of("org.graylog2.inputs.raw.udp.RawUDPInput"), ValueReference.of(false), Collections.emptyList()), JsonNode.class)).build();
        final Optional<NativeEntity<InputWithExtractors>> existingInput = facade.findExisting(entity, Collections.emptyMap());
        assertThat(existingInput).isEmpty();
    }

    @Test
    @UsingDataSet(locations = "/org/graylog2/contentpacks/inputs.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void resolveEntityDescriptor() {
        final EntityDescriptor descriptor = EntityDescriptor.create("5acc84f84b900a4ff290d9a7", INPUT_V1);
        final Graph<EntityDescriptor> graph = facade.resolveNativeEntity(descriptor);
        assertThat(graph.nodes()).containsOnly(descriptor);
    }

    @Test
    @UsingDataSet(locations = "/org/graylog2/contentpacks/inputs.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void resolveEntity() {
        final Map<String, Object> configuration = new HashMap<>();
        configuration.put("override_source", null);
        configuration.put("recv_buffer_size", 262144);
        configuration.put("bind_address", "127.0.0.1");
        configuration.put("port", 5555);
        configuration.put("number_worker_threads", 8);
        final Entity entity = EntityV1.builder().id(ModelId.of("5acc84f84b900a4ff290d9a7")).type(INPUT_V1).data(objectMapper.convertValue(InputEntity.create(ValueReference.of("Local Raw UDP"), ReferenceMapUtils.toReferenceMap(configuration), Collections.emptyMap(), ValueReference.of("org.graylog2.inputs.raw.udp.RawUDPInput"), ValueReference.of(false), Collections.emptyList()), JsonNode.class)).build();
        final Graph<Entity> graph = facade.resolveForInstallation(entity, Collections.emptyMap(), Collections.emptyMap());
        assertThat(graph.nodes()).containsOnly(entity);
    }

    @Test
    @UsingDataSet(locations = "/org/graylog2/contentpacks/inputs.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void delete() throws NotFoundException {
        final Input input = inputService.find("5acc84f84b900a4ff290d9a7");
        final InputWithExtractors inputWithExtractors = InputWithExtractors.create(input);
        assertThat(inputService.totalCount()).isEqualTo(4L);
        facade.delete(inputWithExtractors);
        assertThat(inputService.totalCount()).isEqualTo(3L);
        assertThatThrownBy(() -> inputService.find("5acc84f84b900a4ff290d9a7")).isInstanceOf(NotFoundException.class);
    }

    @Test
    @UsingDataSet(locations = "/org/graylog2/contentpacks/inputs.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void resolveNativeEntityGrokPattern() throws NotFoundException {
        final Input input = inputService.find("5ae2ebbeef27464477f0fd8b");
        EntityDescriptor entityDescriptor = EntityDescriptor.create(ModelId.of(input.getId()), INPUT_V1);
        EntityDescriptor expectedDescriptor = EntityDescriptor.create(ModelId.of("1"), GROK_PATTERN_V1);
        Graph<EntityDescriptor> graph = facade.resolveNativeEntity(entityDescriptor);
        assertThat(graph.nodes()).contains(expectedDescriptor);
    }

    @Test
    @UsingDataSet(locations = "/org/graylog2/contentpacks/inputs.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void resolveNativeEntityLookupTable() throws NotFoundException {
        Mockito.when(lookupuptableBuilder.lookupTable("whois")).thenReturn(lookupuptableBuilder);
        Mockito.when(lookupuptableBuilder.lookupTable("tor-exit-node-list")).thenReturn(lookupuptableBuilder);
        Mockito.when(lookupuptableBuilder.build()).thenReturn(lookupTable);
        Mockito.when(lookupTableService.newBuilder()).thenReturn(lookupuptableBuilder);
        Mockito.when(lookupTableService.hasTable("whois")).thenReturn(true);
        Mockito.when(lookupTableService.hasTable("tor-exit-node-list")).thenReturn(true);
        Mockito.when(lookupTableWhois.id()).thenReturn("dead-beef");
        Mockito.when(dbLookupTableService.get("whois")).thenReturn(Optional.of(lookupTableWhois));
        Mockito.when(lookupTableTor.id()).thenReturn("dead-feed");
        Mockito.when(dbLookupTableService.get("tor-exit-node-list")).thenReturn(Optional.of(lookupTableTor));
        final Input input = inputService.find("5ae2eb0a3d27464477f0fd8b");
        EntityDescriptor entityDescriptor = EntityDescriptor.create(ModelId.of(input.getId()), INPUT_V1);
        EntityDescriptor expectedEntitiyDescriptorWhois = EntityDescriptor.create(ModelId.of("dead-beef"), LOOKUP_TABLE_V1);
        EntityDescriptor expectedEntitiyDescriptorTor = EntityDescriptor.create(ModelId.of("dead-feed"), LOOKUP_TABLE_V1);
        Graph<EntityDescriptor> graph = facade.resolveNativeEntity(entityDescriptor);
        assertThat(graph.nodes()).contains(expectedEntitiyDescriptorWhois);
        assertThat(graph.nodes()).contains(expectedEntitiyDescriptorTor);
    }

    @Test
    @UsingDataSet(locations = "/org/graylog2/contentpacks/inputs.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void resolveForInstallationLookupTable() throws NotFoundException {
        Mockito.when(lookupuptableBuilder.lookupTable("whois")).thenReturn(lookupuptableBuilder);
        Mockito.when(lookupuptableBuilder.lookupTable("tor-exit-node-list")).thenReturn(lookupuptableBuilder);
        Mockito.when(lookupuptableBuilder.build()).thenReturn(lookupTable);
        Mockito.when(lookupTableService.newBuilder()).thenReturn(lookupuptableBuilder);
        Mockito.when(lookupTableService.hasTable("whois")).thenReturn(true);
        Mockito.when(lookupTableService.hasTable("tor-exit-node-list")).thenReturn(true);
        final Input input = inputService.find("5ae2eb0a3d27464477f0fd8b");
        final Map<String, Object> lookupTableConfig = new HashedMap(1);
        lookupTableConfig.put("lookup_table_name", "tor-exit-node-list");
        final ConverterEntity converterEntity = ConverterEntity.create(ValueReference.of(LOOKUP_TABLE.name()), ReferenceMapUtils.toReferenceMap(lookupTableConfig));
        final List<ConverterEntity> converterEntities = new ArrayList<>(1);
        converterEntities.add(converterEntity);
        final InputWithExtractors inputWithExtractors = InputWithExtractors.create(input, inputService.getExtractors(input));
        final LookupTableExtractor extractor = ((LookupTableExtractor) (inputWithExtractors.extractors().iterator().next()));
        final ExtractorEntity extractorEntity = ExtractorEntity.create(ValueReference.of(extractor.getTitle()), ValueReference.of(extractor.getType()), ValueReference.of(extractor.getCursorStrategy()), ValueReference.of(extractor.getTargetField()), ValueReference.of(extractor.getSourceField()), ReferenceMapUtils.toReferenceMap(extractor.getExtractorConfig()), converterEntities, ValueReference.of(extractor.getConditionType()), ValueReference.of(extractor.getConditionValue()), ValueReference.of(extractor.getOrder()));
        List<ExtractorEntity> extractors = new ArrayList<>();
        extractors.add(extractorEntity);
        InputEntity inputEntity = InputEntity.create(ValueReference.of(input.getTitle()), ReferenceMapUtils.toReferenceMap(input.getConfiguration()), Collections.emptyMap(), ValueReference.of(input.getType()), ValueReference.of(input.isGlobal()), extractors);
        final Entity entity = EntityV1.builder().id(ModelId.of(input.getId())).type(INPUT_V1).data(objectMapper.convertValue(inputEntity, JsonNode.class)).build();
        final LookupTableEntity whoIsEntity = LookupTableEntity.create(ValueReference.of("whois"), ValueReference.of("title"), ValueReference.of("description"), ValueReference.of("cache_name"), ValueReference.of("dataadapter_name"), ValueReference.of("default_single_value"), ValueReference.of("BOOLEAN"), ValueReference.of("default_multi_value"), ValueReference.of("BOOLEAN"));
        final LookupTableEntity torNodeEntity = LookupTableEntity.create(ValueReference.of("tor-exit-node-list"), ValueReference.of("title"), ValueReference.of("description"), ValueReference.of("cache_name"), ValueReference.of("dataadapter_name"), ValueReference.of("default_single_value"), ValueReference.of("BOOLEAN"), ValueReference.of("default_multi_value"), ValueReference.of("BOOLEAN"));
        final Entity expectedWhoIsEntity = EntityV1.builder().id(ModelId.of("dead-beef")).data(objectMapper.convertValue(whoIsEntity, JsonNode.class)).type(LOOKUP_TABLE_V1).build();
        final Entity expectedTorEntity = EntityV1.builder().id(ModelId.of("dead-feed")).data(objectMapper.convertValue(torNodeEntity, JsonNode.class)).type(LOOKUP_TABLE_V1).build();
        final EntityDescriptor whoisDescriptor = expectedWhoIsEntity.toEntityDescriptor();
        final EntityDescriptor torDescriptor = expectedTorEntity.toEntityDescriptor();
        final Map<EntityDescriptor, Entity> entityDescriptorEntityMap = new HashMap<>(2);
        entityDescriptorEntityMap.put(whoisDescriptor, expectedWhoIsEntity);
        entityDescriptorEntityMap.put(torDescriptor, expectedTorEntity);
        Graph<Entity> graph = facade.resolveForInstallation(entity, Collections.emptyMap(), entityDescriptorEntityMap);
        assertThat(graph.nodes()).contains(expectedWhoIsEntity);
        assertThat(graph.nodes()).contains(expectedTorEntity);
    }

    @Test
    @UsingDataSet(locations = "/org/graylog2/contentpacks/inputs.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void resolveForInstallationGrokPattern() throws NotFoundException {
        final Input input = inputService.find("5ae2ebbeef27464477f0fd8b");
        final InputWithExtractors inputWithExtractors = InputWithExtractors.create(input, inputService.getExtractors(input));
        final GrokExtractor grokExtractor = ((GrokExtractor) (inputWithExtractors.extractors().iterator().next()));
        final ExtractorEntity extractorEntity = ExtractorEntity.create(ValueReference.of(grokExtractor.getTitle()), ValueReference.of(grokExtractor.getType()), ValueReference.of(grokExtractor.getCursorStrategy()), ValueReference.of(grokExtractor.getTargetField()), ValueReference.of(grokExtractor.getSourceField()), ReferenceMapUtils.toReferenceMap(grokExtractor.getExtractorConfig()), Collections.emptyList(), ValueReference.of(grokExtractor.getConditionType()), ValueReference.of(grokExtractor.getConditionValue()), ValueReference.of(grokExtractor.getOrder()));
        List<ExtractorEntity> extractorEntities = new ArrayList<>(1);
        extractorEntities.add(extractorEntity);
        InputEntity inputEntity = InputEntity.create(ValueReference.of(input.getTitle()), ReferenceMapUtils.toReferenceMap(input.getConfiguration()), Collections.emptyMap(), ValueReference.of(input.getType()), ValueReference.of(input.isGlobal()), extractorEntities);
        Entity entity = EntityV1.builder().id(ModelId.of(input.getId())).type(INPUT_V1).data(objectMapper.convertValue(inputEntity, JsonNode.class)).build();
        final GrokPatternEntity grokPatternEntity = GrokPatternEntity.create("GREEDY", ".*");
        final Entity expectedEntity = EntityV1.builder().id(ModelId.of("dead-feed")).data(objectMapper.convertValue(grokPatternEntity, JsonNode.class)).type(GROK_PATTERN_V1).build();
        final EntityDescriptor entityDescriptor = expectedEntity.toEntityDescriptor();
        final Map<EntityDescriptor, Entity> entities = new HashMap<>(1);
        entities.put(entityDescriptor, expectedEntity);
        Graph<Entity> graph = facade.resolveForInstallation(entity, Collections.emptyMap(), entities);
        assertThat(graph.nodes()).contains(expectedEntity);
    }
}

