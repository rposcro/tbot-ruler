package com.tbot.ruler.newy.services;

import com.tbot.ruler.things.Collector;
import com.tbot.ruler.things.ItemId;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ItemBindingsService {

    public List<Collector> findBindedCollectors(ItemId senderId) {
        return Collections.emptyList();
    }
}
