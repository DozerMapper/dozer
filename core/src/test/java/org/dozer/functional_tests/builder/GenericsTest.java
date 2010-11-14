package org.dozer.functional_tests.builder;

import org.dozer.DozerBeanMapper;
import org.dozer.loader.api.BeanMappingBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * @author dmitry.buzdin
 */
public class GenericsTest extends Assert {

  private DozerBeanMapper mapper;

  @Before
  public void setUp() {
    mapper = new DozerBeanMapper();
  }


  @Test
  public void shouldDetermineCollectionTypeViaFieldGenericType() {
    mapper.addMapping(new BeanMappingBuilder() {
      @Override
      protected void configure() {
        mapping(Container.class, ContainerDTO.class)
                .fields(field("items").accessible(true), "items");
      }
    });

    Container container = prepareContainer();

    Container containerSpy = spy(container);
    when(containerSpy.getItems()).thenThrow(new IllegalStateException());

    ContainerDTO result = mapper.map(containerSpy, ContainerDTO.class);

    assertDto(result);
  }

  @Test
  public void shouldDetermineCollectionTypeViaGetter() {
    mapper.addMapping(new BeanMappingBuilder() {
      @Override
      protected void configure() {
        mapping(Container.class, ContainerDTO.class)
                .fields("items", "items");
      }
    });

    Container container = prepareContainer();

    ContainerDTO result = mapper.map(container, ContainerDTO.class);

    assertDto(result);
  }

  private void assertDto(ContainerDTO result) {
    assertNotNull(result);
    assertEquals(1, result.getItems().size());
    assertEquals("A", result.getItems().get(0).getId());
  }

  private Container prepareContainer() {
    Container container = new Container();
    ArrayList<Item> items = new ArrayList<Item>();
    Item item = new Item();
    item.setId("A");
    items.add(item);
    container.items = items;
    return container;
  }

  public static class Container {
    List<Item> items;

    public List<Item> getItems() {
      return items;
    }

  }

  public static class ContainerDTO {
    private List<ItemDTO> items;

    public List<ItemDTO> getItems() {
      return items;
    }

    public void setItems(List<ItemDTO> items) {
      this.items = items;
    }
  }

  public static class Item {
    private String id;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }

  public static class ItemDTO {
    private String id;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }
  }


}
