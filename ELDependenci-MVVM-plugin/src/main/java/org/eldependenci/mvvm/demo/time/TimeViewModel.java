package org.eldependenci.mvvm.demo.time;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.eldependenci.mvvm.ELDMVVMPlugin;
import org.eldependenci.mvvm.model.State;
import org.eldependenci.mvvm.viewmodel.ViewModel;
import org.eldependenci.mvvm.viewmodel.ViewModelBinding;

import javax.inject.Inject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@ViewModelBinding(TimeView.class)
public class TimeViewModel implements ViewModel {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm:ss");

    private final BukkitTask task;

    @State
    private TimeStateHolder holder;

    @Inject
    public TimeViewModel(ELDMVVMPlugin plugin){
        task = new TimerRunnable().runTaskTimer(plugin, 20L, 20L);
    }


    @Override
    public void init(Player player) {
        holder.setTime(TIME_FORMATTER.format(LocalTime.now()));
        holder.setDuration(0);
    }

    @Override
    public void beforeUnMount(Player player) {
        if (!task.isCancelled()) task.cancel();
    }

    private class TimerRunnable extends BukkitRunnable {

        private int i = 0;

        @Override
        public void run() {
            holder.setTime(TIME_FORMATTER.format(LocalTime.now()));
            holder.setDuration(i++);
        }
    }
}
