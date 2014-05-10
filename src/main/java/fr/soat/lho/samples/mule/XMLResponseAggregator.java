package fr.soat.lho.samples.mule;

import java.util.Iterator;
import java.util.List;

import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.MuleMessageCollection;
import org.mule.api.store.ObjectStoreException;
import org.mule.routing.AbstractCorrelationAggregator;
import org.mule.routing.AggregationException;
import org.mule.routing.EventGroup;
import org.mule.transport.NullPayload;

public class XMLResponseAggregator extends AbstractCorrelationAggregator {

	@Override
	protected MuleEvent aggregateEvents(EventGroup events)
			throws AggregationException {
        StringBuilder aggregateResponse = new StringBuilder();
        MuleEvent event = null;

        try {
        	for (Iterator<MuleEvent> iterator = events.iterator(); iterator.hasNext();) {
        		event = iterator.next();
        		try {
        			MuleMessage message = event.getMessage();
					System.out.println("//TODO: HOUSSOU message => " + message + "type => " + message.getClass());
					doAggregate(aggregateResponse, message);
        		} catch (Exception e) {
        			throw new AggregationException(events, null ,e);
        		}
        	}
        	System.out.println("//TODO: HOUSSOU aggregateResponse => " + aggregateResponse);
            return new DefaultMuleEvent(new DefaultMuleMessage(aggregateResponse, events.toMessageCollection()
                .getMuleContext()), events.getMessageCollectionEvent());
        } catch (ObjectStoreException e) {
            throw new AggregationException(events,null);
        }
	}

	private void doAggregate(StringBuilder aggregateResponse, MuleMessage message)
			throws Exception {
		String payloadAsString = null;
		if (message instanceof MuleMessageCollection) {
			for (MuleMessage payload : ((MuleMessageCollection) message).getMessagesAsArray()) {							
				doAggregate(aggregateResponse, payload);
			}
		} else {
			if (message.getPayload() instanceof List) {
				for (Object payload : message.getPayload(List.class)) {
					payloadAsString = /*(payload == null || payload instanceof NullPayload) ? null :*/ message.getPayloadAsString();
					aggregateResponse.append(payloadAsString == null ? "" : payloadAsString);
				}
			} else {
				Object payload = message.getPayload();
				payloadAsString = /*(payload == null || payload instanceof NullPayload) ? null :*/ message.getPayloadAsString();
				aggregateResponse.append(payloadAsString == null ? "" : payloadAsString);
			}
		}
	}
}
