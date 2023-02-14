package bgu.spl.net.impl.stomp;

import java.util.ArrayList;
import java.util.List;

public class channel {
    
        protected String channelName;
        protected List<String> subscribersList;
    
        public channel(String channelName) {
            this.channelName = channelName;
            subscribersList = new ArrayList<String>();
        }
    
        public String getchannelName() {
            return channelName;
        }
    
        public List<String> getSubscribersList() {
            return subscribersList;
        }
    
        public void addSubscriber(String subscriber) {
            subscribersList.add(subscriber);
        }
    
        public void removeSubscriber(String subscriber) {
            subscribersList.remove(subscriber);
        }
}
