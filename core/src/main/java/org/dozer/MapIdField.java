package org.dozer;

import java.util.HashMap;
import java.util.Map;

/**
 * Only intended for internal use. Simple helper class to keep track of mapped objects
 * and their mapIds.
 *
 * @author andino.alexander
 *
 */
public class MapIdField {

  // <mapIdOfObject, mappedObject>
  private Map<String, Object> mappedObjects;

  public MapIdField() {
    mappedObjects = new HashMap<String, Object>();
  }

  public void put(String mapId, Object value) {
    mappedObjects.put(mapId, value);
  }

  public Object get(String mapId) {
    return mappedObjects.get(mapId);
  }

  public boolean containsMapId(String mapId) {
    return mappedObjects.containsKey(mapId);
  }
}
