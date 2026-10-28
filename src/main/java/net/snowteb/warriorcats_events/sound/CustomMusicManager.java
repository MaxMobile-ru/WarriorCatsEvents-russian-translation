package net.snowteb.warriorcats_events.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.snowteb.warriorcats_events.WarriorCatsEvents;
import net.snowteb.warriorcats_events.client.ClientTerritoryEvents;
import net.snowteb.warriorcats_events.client.HUDClientMessage;
import net.snowteb.warriorcats_events.zconfig.WCEClientConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = WarriorCatsEvents.MODID, value = Dist.CLIENT)
public class CustomMusicManager {

    private static final LastPlayed LAST_PLAYED = new LastPlayed();

    private static int nextSongDelay = 100;

    public static List<CustomMusics.BackgroundMusic> getPlaylist() {
        return List.of(
                CustomMusics.MUSIC_ANCIENT_TUNNELS,
                CustomMusics.MUSIC_CROSSING_THE_RIVER,
                CustomMusics.MUSIC_DAWN_PATROL,
                CustomMusics.MUSIC_LEAFBARE,
                CustomMusics.MUSIC_MOONHIGH_VIGIL,
                CustomMusics.MUSIC_NEWLEAF,
                CustomMusics.MUSIC_THE_MEDICINE_CATS_DEN,
                CustomMusics.MUSIC_THE_MOONSTONE,
                CustomMusics.MUSIC_DOES_IT_HAVE_TO_END_ALREADY,
                CustomMusics.MUSIC_I_MISS_YOU_ALREADY,
                CustomMusics.MUSIC_I_WANTED_IT_TO_BE_YOU,
                CustomMusics.MUSIC_OH_TO_RELIVE_IT_ONCE_MORE,
                CustomMusics.MUSIC_YOU_USED_TO_REMEMBER
        );
    }

    public static List<Music> getPlaylistTracks() {
        return getPlaylist().stream().map(CustomMusics.BackgroundMusic::music).collect(Collectors.toList());
    }

    private static boolean isMusic(Music music, SoundInstance sound) {
        if (music == null || sound == null) return false;
        return music.getEvent().value().getLocation().equals(sound.getLocation());
    }

    private static boolean isJukeboxPlaying(SoundEngine engine) {
        return ((SoundEngineAccess) engine).wce$instanceToChannel().keySet().stream()
                .anyMatch(instance -> instance.getSource() == SoundSource.RECORDS);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        RandomSource random = mc.level.random;
        MusicManager musicManager = mc.getMusicManager();

        if (musicManager instanceof MusicManagerAccess access) {
            SoundInstance current = access.wce$getCurrentMusic();
            SoundEngine engine = ((SoundManagerAccess) Minecraft.getInstance().getSoundManager()).wce$soundEngine();

            if (current instanceof BGMusicSoundInstance) {
                if (!mc.getSoundManager().isActive(current)) {
                    Minecraft.getInstance().execute(musicManager::stopPlaying);
                }
            }

            if (current instanceof BGMusicSoundInstance bgMusicSoundInstance) {
                bgMusicSoundInstance.tick();

                ChannelAccess.ChannelHandle handle = ((SoundEngineAccess) engine).wce$instanceToChannel().get(bgMusicSoundInstance);
                if (handle != null) {
                    handle.execute(channel -> {
                        channel.setVolume(bgMusicSoundInstance.getModifiedVolume());
                    });
                }

            }

            if (current != null && access.wce$isPlaylistPlaying(getPlaylistTracks())) {
                LAST_PLAYED.put(current);
            }

            if (isJukeboxPlaying(engine)) {
                if (access.wce$isPlaylistPlaying(getPlaylistTracks())) {
                    Minecraft.getInstance().execute(() -> {
                        if (current instanceof BGMusicSoundInstance bgMusicSoundInstance) {
                            bgMusicSoundInstance.stop();
                        } else {
                            musicManager.stopPlaying();
                        }
                    });
                }
                return;
            }

            if (ClientTerritoryEvents.isIsInATerritory() && WCEClientConfig.CLIENT.AMBIENT_MUSIC.get()) {
                if (!access.wce$isPlaylistPlaying(getPlaylistTracks())) {

                    if (nextSongDelay > 0) {
                        nextSongDelay--;
                        return;
                    }

                    List<CustomMusics.BackgroundMusic> playlist = getPlaylist();
                    CustomMusics.BackgroundMusic toPlay;
                    if (playlist.size() <= 1) {
                        toPlay = playlist.get(random.nextInt(playlist.size()));
                    } else {
                        List<CustomMusics.BackgroundMusic> candidates = playlist.stream()
                                .filter(bgm -> !LAST_PLAYED.has(bgm.music())).toList();

                        toPlay = candidates.get(random.nextInt(candidates.size()));
                    }

                    Minecraft.getInstance().execute(() -> {
                        musicManager.stopPlaying();
                        access.wce$playBackgroundSong(toPlay.music());
                        HUDClientMessage.send(toPlay.message());
                    });

                    nextSongDelay = Mth.nextInt(random, toPlay.music().getMinDelay(), toPlay.music().getMaxDelay());
                }
            } else {
                if (nextSongDelay > 0) nextSongDelay = 0;

                if (access.wce$isPlaylistPlaying(getPlaylistTracks())) {
                    Minecraft.getInstance().execute(() -> {
                        if (current instanceof BGMusicSoundInstance bgMusicSoundInstance) {
                            bgMusicSoundInstance.stop();
                        } else {
                            musicManager.stopPlaying();
                        }
                    });
                }
            }
        }
    }

    private static class LastPlayed {
        List<SoundInstance> lastPlayedSongs = new ArrayList<>();

        public void put(SoundInstance song) {
            if (lastPlayedSongs.size() >= 2) {
                lastPlayedSongs.remove(0);
            }
            lastPlayedSongs.add(song);
        }

        public boolean has(Music track) {
            return lastPlayedSongs.stream().anyMatch(soundInstance -> isMusic(track, soundInstance));
        }
    }
}
