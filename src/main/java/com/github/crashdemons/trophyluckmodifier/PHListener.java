/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.trophyluckmodifier;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.shininet.bukkit.playerheads.events.HeadRollEvent;
import org.shininet.bukkit.playerheads.events.modifiers.DropRateModifier;

/**
 *
 * @author crashdemons <crashenator at gmail.com>
 */
public class PHListener implements Listener{
    private final TrophyLuckModifier plugin;
    PHListener(TrophyLuckModifier plugin){
        this.plugin=plugin;
    }
    
    @EventHandler
    public void onRoll(HeadRollEvent event){
        if(!plugin.hasPlayerheads()) return;
        //System.out.println("PH roll");
        
        //handle the roll with an adapter class since we can't make these two events implement a common interface without reflection/proxy
        RollEventAdapter adaptedRoll = new RollEventAdapter(event);
        DropRateModifier mod = plugin.getModification(adaptedRoll).toPH();
        event.setCustomModifier(plugin, "luck", mod);
        event.recalculateSuccess();
    }
}
