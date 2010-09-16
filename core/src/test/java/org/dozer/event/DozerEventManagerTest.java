package org.dozer.event;

import org.dozer.DozerEventListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

/**
 * @author Dmitry Buzdin
 */
public class DozerEventManagerTest extends Assert {

  private DozerEventManager manager;
  private ArrayList<DozerEventListener> listeners;

  @Before
  public void setUp() throws Exception {
    listeners = new ArrayList<DozerEventListener>();
    manager = new DozerEventManager(listeners);
  }

  @Test
  public void testFireEvent_NoListeners() throws Exception {
    DozerEventListener listener = mock(DozerEventListener.class);
    listeners.add(listener);

    DozerEvent dozerEvent = mock(DozerEvent.class);
    when(dozerEvent.getType()).thenReturn(DozerEventType.MAPPING_STARTED);

    manager.fireEvent(dozerEvent);
    
    verify(listener).mappingStarted(dozerEvent);
  }


}
