/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/ .
 */
package com.github.crashdemons.trophyluckmodifier;

import com.github.crashdemons.miningtrophies.events.TrophyRollEvent;
import org.bukkit.entity.Entity;
import org.shininet.bukkit.playerheads.events.HeadRollEvent;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public class RollEventAdapter {
    private final boolean alwaysRewarded;
    private final double roll;
    private final double droprate;
    private final RollType type;
    private boolean success;
    private Entity entity;
    public RollEventAdapter(HeadRollEvent event){
        alwaysRewarded = event.getKillerAlwaysBeheads();
        roll = event.getEffectiveDropRoll();
        droprate = event.getEffectiveDropRate();
        success=event.getDropSuccess();
        type = RollType.PLAYERHEADS;
        entity = event.getKiller();
    }
    public RollEventAdapter(TrophyRollEvent event){
        alwaysRewarded = event.getMinerAlwaysRewarded();
        roll = event.getEffectiveDropRoll();
        droprate = event.getEffectiveDropRate();
        success=event.getDropSuccess();
        type = RollType.MININGTROPHIES;
        entity = event.getMiner();
    }
    public Entity getEntity(){ return entity; }
    public RollType getType(){ return type; }
    public boolean getAlwaysRewarded(){ return alwaysRewarded; }
    public double getEffectiveDropRoll(){ return roll; }
    public double getEffectiveDropRate(){ return droprate; }
    public boolean succeeded(){ return success; }
    public void setSuccess(boolean success){ this.success = success; }
}
