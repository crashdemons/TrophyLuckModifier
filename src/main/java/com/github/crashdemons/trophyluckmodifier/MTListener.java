/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.trophyluckmodifier;

import com.github.crashdemons.miningtrophies.events.TrophyRollEvent;
import com.github.crashdemons.miningtrophies.modifiers.DropRateModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author crashdemons <crashenator at gmail.com>
 */
public class MTListener implements Listener{
    private final TrophyLuckModifier plugin;
    MTListener(TrophyLuckModifier plugin){
        this.plugin=plugin;
    }
    
    @EventHandler
    public void onRoll(TrophyRollEvent event){
        if(!plugin.hasMiningtrophies()) return;
        //System.out.println("MT roll");
        
        //handle the roll with an adapter class since we can't make these two events implement a common interface without reflection/proxy
        
        //handle the roll with an adapter class since we can't make these two events implement a common interface without reflection/proxy
        RollEventAdapter adaptedRoll = new RollEventAdapter(event);
        DropRateModifier mod = plugin.getModification(adaptedRoll).toMT();
        event.setCustomModifier(plugin, "luck", mod);
        event.recalculateSuccess();
    }
    
}
