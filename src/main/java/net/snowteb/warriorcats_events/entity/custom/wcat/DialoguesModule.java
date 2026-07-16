package net.snowteb.warriorcats_events.entity.custom.wcat;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.snowteb.warriorcats_events.clan.WCEPlayerData;
import net.snowteb.warriorcats_events.clan.WCEPlayerDataProvider;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity.Rank.APPRENTICE;
import static net.snowteb.warriorcats_events.entity.custom.wcat.WCatEntity.Rank.KIT;

public class DialoguesModule {
    private final WCatEntity cat;
    public DialoguesModule(WCatEntity cat) {
        this.cat = cat;
    }

    void loadDialogueMap() {

        cat.dialoguePool.put("CALM.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Oh! Thank you, <morph.name>!",
                "It looks good! I appreciate it!",
                "It's just what I needed, thank you, <<morph.name>!",
                "Aww! This is exactly what I wanted!",
                "I will definitely enjoy this.",
                "You are so kind! Thank you!",
                "That's very kind of you, <morph.name>.",
                "You brought this for me? That’s kind of you, <morph.name>.",
                "This came at a good time. Thank you.",
                "I’m glad you shared this with me. Thanks.",
                "I didn’t expect this, but I appreciate it a lot."
        ));
        cat.dialoguePool.put("CALM.GIVE_ITEM.FAIL", Arrays.asList(
                "Oh, I'm good, thank you anyway.",
                "Err.. Thanks, but maybe for later?",
                "I don't really need that.",
                "Don't worry, you can keep it.",
                "Maybe give this to someone else.",
                "Thanks, but it's unnecessary.",
                "I'll pass for now, thank you!",
                "No, thank you. I don’t really need it.",
                "I’m fine without it, but thank you.",
                "Maybe save it for someone who needs it more.",
                "Another time, perhaps."
        ));

        cat.dialoguePool.put("CALM.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "Oh! That's comforting. Thank you, <morph.name>.",
                "I appreciate your kindness.",
                "I appreciate you too!",
                "That feels nice!",
                "Thank you, <morph.name>. That makes me feel better",
                "You’re very considerate, <morph.name>."
        ));
        cat.dialoguePool.put("CALM.SHOW_AFFECTION.FAIL", Arrays.asList(
                "Not right now, please.",
                "I'm not really in the mood for that...",
                "Please give me some space.",
                "Er.. Okay?",
                "Please don't-",
                "I appreciate my personal space more for now.",
                "I appreciate it, but I’m not in the mood.",
                "Another time, maybe.",
                "I’d prefer some distance for now."
        ));

        cat.dialoguePool.put("CALM.TALK.SUCCESS", Arrays.asList(
                "It's a good day, isn't it, <morph.name>?",
                "The wind feels peaceful today.",
                "I've been thinking… Things are finally settling down.",
                "I enjoy moments like this. Quiet. Simple.",
                "You are always so serene… it's comforting.",
                "I remember when I was a kit... Oh, no I don't remember-",
                "Did you see that huge mouse today, <morph.name>?",
                "I smell rain, don't you, <morph.name>?",
                "I once caught a pigeon the size of a cat!",
                "You know... Sometimes I feel like the trees are whispering... Are they, <morph.name>?",
                "This place feels quiet today, doesn’t it, <morph.name>?"
        ));
        cat.dialoguePool.put("CALM.TALK.FAIL", Arrays.asList(
                "I'm... not really in the mood to talk right now.",
                "Let's leave it for later.",
                "I’d rather stay quiet for now.",
                "I need some space to think.",
                "Not now, please.",
                "Sorry, not right now.",
                "Sorry, what did you say?",
                "Let’s talk another time.",
                "Sorry… I’m focusing on something.",
                "Maybe later. I’m not in a talking mood."
        ));


        cat.dialoguePool.put("GRUMPY.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Fine… I guess this is alright.",
                "Hmph. Thanks… I suppose.",
                "It'll do.",
                "Yeah… thanks, <morph.name>.",
                "I might use this, but don't get used to it.",
                "Well, something good at last.",
                "As long as it's not crow-food...",
                "Huh… better than nothing.",
                "I’ve seen worse. Thanks, <morph.name>.",
                "Okay… I’ll admit it’s decent."
        ));
        cat.dialoguePool.put("GRUMPY.GIVE_ITEM.FAIL", Arrays.asList(
                "I don't want that.",
                "Don't you have anything better to do?",
                "Why would I want this?",
                "Take it back.",
                "Nah.",
                "It smells like crow-food...",
                "Uh, I'll pass.",
                "Someone else might eat this, but not me.",
                "Give it to someone else.",
                "Do I have to?",
                "No thanks."
        ));

        cat.dialoguePool.put("GRUMPY.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "Tch… fine. Just for a second.",
                "Don't make a big deal out of it.",
                "…Thanks.",
                "Alright. That's enough.",
                "Whatever. I don't hate it.",
                "I... Okay?",
                "Alright… if it makes you feel better.",
                "Just this once.",
                "Okay… but only for a moment.",
                "…Thanks, I guess.",
                "Hmph… fine."
        ));
        cat.dialoguePool.put("GRUMPY.SHOW_AFFECTION.FAIL", Arrays.asList(
                "Paws off.",
                "No touchies.",
                "Don't, thank you.",
                "Back away.",
                "I said no.",
                "I'm not that type of cat.",
                "I'd rather eat crow-food.",
                "Get off, mouse-brain.",
                "Not happening.",
                "Find someone else for that."
        ));

        cat.dialoguePool.put("GRUMPY.TALK.SUCCESS", Arrays.asList(
                "I smell trouble today, don't you, <morph.name>?",
                "See that cat over there? They are mouse-brained.",
                "If anything causes trouble, they'll have to answer to my claws.",
                "Tch… everything feels off around here.",
                "If something goes wrong, I’ll be ready.",
                "Once, I heard rustling in the grass behind me. I just stood still. Turned out to be a mouse. Still didn’t like that it got so close without me knowing.",
                "This place smelled better yesterday...",
                "I don’t enjoy company… but you’re tolerable, <morph.name>.",
                "I don’t trust the quiet… Do you, <morph.name>?",
                "I don’t like how the territory feels lately.",
                "Hey, <morph.name>, how is prey running?",
                "I once clawed a cat's ears off for getting too close. Beware.",
                "Nothing stays peaceful for long."
        ));
        cat.dialoguePool.put("GRUMPY.TALK.FAIL", Arrays.asList(
                "Hmph. Don’t bother me.",
                "I’m not here to chat.",
                "Chatting catches no prey, you should know it.",
                "Save it.",
                "Not interested.",
                "Go talk to someone else.",
                "Someone else might want to hear all your meowing.",
                "I don’t feel like talking.",
                "Go bother someone else.",
                "I’ve got better things to do.",
                "Leave the chatter for someone else."
        ));


        cat.dialoguePool.put("CAUTIOUS.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Thank you, <morph.name>. I appreciate it.",
                "Smells nice, thank you!.",
                "Seems fine, thanks.",
                "Alright… I’ll take it.",
                "Thanks, <morph.name>.",
                "I appreciate the gesture, <morph.name>.",
                "I’ll take it. Thanks, <morph.name>.",
                "Thanks. I wasn’t expecting this, but I’m grateful.",
                "Did you catch this yourself? Thanks, <morph.name>.",
                "If you're sure... I'll take it."
        ));
        cat.dialoguePool.put("CAUTIOUS.GIVE_ITEM.FAIL", Arrays.asList(
                "I'd rather not take that for now.",
                "Err... No, thanks, <morph.name>.",
                "No… sorry.",
                "No, you can have it, <morph.name>.",
                "Better not.",
                "Maybe another time.",
                "I appreciate the thought, but I’ll refuse this time.",
                "Better if someone else takes it instead.",
                "Oh! No, you can keep it. Thanks anyway.",
                "I think I’ll pass for now."
        ));

        cat.dialoguePool.put("CAUTIOUS.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "…Alright. That’s okay.",
                "I’m okay with that.",
                "That’s alright.",
                "That feels… alright.",
                "Well... I think I trust you, <morph.name>.",
                "Thank you. I’m comfortable.",
                "I’m okay with this… I think.",
                "Thank you… That was reassuring.",
                "That's sweet of you, <morph.name>."
        ));
        cat.dialoguePool.put("CAUTIOUS.SHOW_AFFECTION.FAIL", Arrays.asList(
                "Too close.",
                "Wait… no.",
                "Please step back.",
                "Not now, sorry.",
                "Err, not for now.",
                "Sorry… I don’t feel comfortable with that.",
                "I’d rather keep some distance for the moment."
        ));

        cat.dialoguePool.put("CAUTIOUS.TALK.SUCCESS", Arrays.asList(
                "Do you… hear that, <morph.name>?",
                "I’ve been keeping my guard up, In case a fox shows up.",
                "I hear things in the night... Don't you, <morph.name>?",
                "I’m watching the area… just in case.",
                "I heard the bushes rustling the other day, didn't you, <morph.name>?",
                "I hope this place is safe enough for us.",
                "Do you like this place too, <morph.name>?",
                "No threats yet... but I’ll stay aware.",
                "Once, I followed scent trail but stopped halfway. The earth dipped suddenly into a hollow. If I had rushed forward, I would’ve slipped... That's why you must always be careful.",
                "I trust careful paws more than lucky ones. Don't you, <morph.name>?",
                "Quiet day so far.",
                "I sometimes mark the places where a loner passes. It sounds strange, I know… but one day it will be of use."
        ));
        cat.dialoguePool.put("CAUTIOUS.TALK.FAIL", Arrays.asList(
                "Not now.",
                "Sorry, I'm up to something.",
                "I need to stay alert, we can talk later.",
                "Let’s talk later… maybe.",
                "I'm a little busy, let's leave it for later.",
                "I’ve been keeping watch… Things feel quiet, but I’m not fully convinced.",
                "Calm days make me think something might be approaching.",
                "Let’s talk later. I’m paying attention to something.",
                "We can leave it for later."
        ));


        cat.dialoguePool.put("INDEPENDENT.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Thanks. I could've gotten it myself… but fine.",
                "I’ll take it this time.",
                "Seems fine. Appreciated.",
                "Thanks, I'll handle the rest on my own.",
                "Thanks, <morph.name>.",
                "Only this once...",
                "Well... Maybe this time. Thanks, <morph.name>.",
                "I’ll accept this time."
        ));
        cat.dialoguePool.put("INDEPENDENT.GIVE_ITEM.FAIL", Arrays.asList(
                "I can hunt by myself.",
                "Keep it. I can manage myself.",
                "Unnecessary.",
                "No. I’ll obtain my own prey.",
                "I prefer independence, thanks anyway.",
                "Keep it. I’ll catch my own.",
                "I’m fine without it.",
                "Maybe someone else might need it."
        ));

        cat.dialoguePool.put("INDEPENDENT.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "…Alright. Just a moment.",
                "You are so sweet, thanks, <morph.name>.",
                "Fine. Briefly.",
                "I can allow this… for now.",
                "Well, it's not that bad.",
                "I appreciate you too, <morph.name>."
        ));
        cat.dialoguePool.put("INDEPENDENT.SHOW_AFFECTION.FAIL", Arrays.asList(
                "Maybe another time.",
                "That kind of closeness isn’t for me.",
                "Personal space, please.",
                "Not right now.",
                "I'm good, thanks."
        ));

        cat.dialoguePool.put("INDEPENDENT.TALK.SUCCESS", Arrays.asList(
                "Greetings, <morph.name>, how is your day?",
                "A good cat can always survive on their own. Don't you think, <morph.name>?",
                "I survived on my own for a long time in the past. Have you, <morph.name>?",
                "Relying on someone else would weaken any cat. Good thing you are different, <morph.name>.",
                "I always prefer to catch my own prey. Don't you, <morph.name>?",
                "The wild always taught me to rely on my own paws.",
                "I remember when I was a kit, I had to learn to hunt on my own.",
                "How is prey running, <morph.name>?",
                "Long time ago I spent a leaf-fall season living near a fallen log. Prey was scarce, but I learned every sound the forest made. Hunger teaches you to listen… and to trust your instincts.",
                "I almost ate deathberries when I was a kit, did you know? I immediately spit them out when they tasted quite... Weird.",
                "I don’t avoid others. I just… return to myself every now and then. The world feels clearer when it’s only you and your paws for a little..."
        ));
        cat.dialoguePool.put("INDEPENDENT.TALK.FAIL", Arrays.asList(
                "I don’t need conversation.",
                "I prefer silence for now.",
                "Not now, I'm quite busy.",
                "I’ll pass for now.",
                "I’m fine on my own for now.",
                "Not in the mood to chat."
        ));


        cat.dialoguePool.put("FRIENDLY.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Wow! Thanks, <morph.name>!",
                "You’re always so thoughtful! Thank you!",
                "It looks good! I appreciate it, <morph.name>!",
                "It's just what I wanted, thank you!",
                "Aww! This is exactly what I wanted!",
                "You're amazing, thank you, <morph.name>!",
                "I love it!",
                "This is great! Thanks, <morph.name>!",
                "You always bring good prey, thanks, <morph.name>."
        ));
        cat.dialoguePool.put("FRIENDLY.GIVE_ITEM.FAIL", Arrays.asList(
                "Err… maybe not the best this time.",
                "That’s kind, but I'm good, thank you, <morph.name>!",
                "I'm good. Thanks anyway!",
                "Maybe next time, okay?",
                "Not right now, but thanks, <morph.name>!",
                "I appreciate it, but I’m fine for now."
        ));

        cat.dialoguePool.put("FRIENDLY.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "That feels nice!",
                "Thank you! You're so sweet, <morph.name>!",
                "I appreciate your company!",
                "This feels good!",
                "It makes me happy. Thank you, <morph.name>!",
                "I really appreciate the care.",
                "You’re good company, <morph.name>."
        ));
        cat.dialoguePool.put("FRIENDLY.SHOW_AFFECTION.FAIL", Arrays.asList(
                "A little too much!",
                "Err… slow down!",
                "Sorry, not right now.",
                "Oh! Too close!",
                "Not right now, sorry!",
                "Not at the moment, sorry.",
                "Maybe later, okay?"
        ));

        cat.dialoguePool.put("FRIENDLY.TALK.SUCCESS", Arrays.asList(
                "Hey! Nice to see you again, <morph.name>!",
                "I was hoping you'd come by!",
                "Hi! how has your day been, <morph.name>?",
                "I was just thinking about you, <morph.name>! How are things going?",
                "Am I crazy or did I smell a fox last night... Anyway, how are you, <morph.name>?",
                "Hey! How is it going, <morph.name>?",
                "I would really love a walk. Wouldn't you?",
                "I’ve had a pretty calm day so far. How about you?",
                "Hi, <morph.name>! Have you seen anything interesting today?",
                "I was just thinking about exploring nearby later."
        ));
        cat.dialoguePool.put("FRIENDLY.TALK.FAIL", Arrays.asList(
                "Sorry, I’m a bit tired to talk.",
                "Maybe later, okay?",
                "We can talk later, sorry, <morph.name>!",
                "I don’t really feel like talking now.",
                "Could we talk another time?",
                "Sorry… I’m a bit worn out."
        ));


        cat.dialoguePool.put("SHY.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Wow… Thank you, <morph.name>.",
                "I… appreciate it.",
                "You didn’t have to…",
                "Thanks… really, <morph.name>.",
                "This means a lot to me. Thank you, <morph.name>.",
                "For me...? Thanks, <morph.name>!",
                "You’re very kind… thank you, <morph.name>.",
                "I didn’t expect this… thank you.",
                "This feels special… thanks.",
                "I’m really thankful, <morph.name>."
        ));
        cat.dialoguePool.put("SHY.GIVE_ITEM.FAIL", Arrays.asList(
                "S-sorry… I can't take it.",
                "I can't take that right now.",
                "I-I'm good, thanks, <morph.name>.",
                "I'm not hungry. Sorry, <morph.name>.",
                "Maybe not now…",
                "I don’t think I should take that."
        ));

        cat.dialoguePool.put("SHY.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "…Okay.",
                "That’s… nice.",
                "Thank you… <morph.name>.",
                "I'm… alright with this.",
                "Oh! You're so sweet, <morph.name>!",
                "It helps… more than I expected."
        ));
        cat.dialoguePool.put("SHY.SHOW_AFFECTION.FAIL", Arrays.asList(
                "N-not right now…",
                "S-sorry, I'm not that affective.",
                "Not now, maybe another time?",
                "Sorry, not now.",
                "Not right now… Sorry.",
                "Maybe not right now…",
                "I'm not really in the mood, sorry."
        ));

        cat.dialoguePool.put("SHY.TALK.SUCCESS", Arrays.asList(
                "E-err… Hi, <morph.name>!",
                "I used to be always alone, talking to the trees, to the moon... Until you came,  <morph.name>, and you made me feel I truly belonged...",
                "I’m glad you’re here, <morph.name>!",
                "Back in the days, I barely spoke to anyone. One evening, another cat sat beside me without saying a word. We just watched the sky. That's something I still remember...",
                "I don’t talk much… but I like listening. How has your day been, <morph.name>?",
                "I once practiced speaking... alone, among some bushes. I repeated greetings to myself until I felt brave enough to use them. I think I've improved, heh...",
                "It's nice being near you. How are you, <morph.name>?",
                "H-hi, <morph.name>! Have you caught any prey today?",
                "Are we going on an adventure? I heard there is so much out there!",
                "Hi <morph.name>! I-I was just watching the clouds.",
                "H-hey! Um… how has your day been?",
                "Sometimes silence feels comfortable... I think I even learned to talk to myself!"
        ));
        cat.dialoguePool.put("SHY.TALK.FAIL", Arrays.asList(
                "I… um… excuse me I got to go-",
                "S-sorry, I can't talk right now",
                "*quietly stares into your soul*",
                "I’d rather stay in the corner for now.",
                "Maybe later, okay?"
        ));


        cat.dialoguePool.put("AMBITIOUS.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Yes! Thank you, <morph.name>!",
                "Good. This will do.",
                "Smells good. Thank you, <morph.name>.",
                "Just what I needed. Thank you, <morph.name>.",
                "I will return the favour."
        ));
        cat.dialoguePool.put("AMBITIOUS.GIVE_ITEM.FAIL", Arrays.asList(
                "Thanks, but I don't need it right now.",
                "Maybe another time.",
                "I might take it later.",
                "Not now, but thanks.",
                "I can catch my own, thanks.",
                "I don’t need that at the moment."
        ));

        cat.dialoguePool.put("AMBITIOUS.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "Alright… just for a moment.",
                "I appreciate your support.",
                "Oh, thank you, <morph.name>.",
                "This makes me feel better.",
                "I'm grateful.",
                "Fine… I’ll allow it.",
                "Thank you, <morph.name>."
        ));
        cat.dialoguePool.put("AMBITIOUS.SHOW_AFFECTION.FAIL", Arrays.asList(
                "Aren't you too close?",
                "No distractions.",
                "A little space, please.",
                "Later. I’m a little busy.",
                "This isn't the time."
        ));

        cat.dialoguePool.put("AMBITIOUS.TALK.SUCCESS", Arrays.asList(
                "I’m going to be the best warrior… you’ll see.",
                "When I was younger... I tried to impress everyone by running ahead of everyone. I slipped and almost ate all the mud from the forest, and I learned something important... Strength is about patience.",
                "The clan needs cats who dream bigger. Like you, <morph.name>.",
                "Someday, I’ll lead something greater.",
                "There’s always more to learn. And there is always a better you to be. Never stop chasing your dreams, <morph.name>.",
                "Those Badgers will never stand a chance!.",
                "I had always wanted to be someone others can rely on. You can always count on me, <morph.name>.",
                "If I see those dogs, I will teach them a lesson!.",
                "How is your day, <morph.name>? Have you thought about expanding our territory?",
                "I don’t dream of power, you know? I dream of being useful when it matters most, of being there for everyone when they need it the most, just as they would be for me."
        ));
        cat.dialoguePool.put("AMBITIOUS.TALK.FAIL", Arrays.asList(
                "I don’t have time for distractions.",
                "I need to focus.",
                "Talking will catch no prey.",
                "Not now. I’m thinking.",
                "We can talk when it's something important."
        ));


        cat.dialoguePool.put("HUMBLE.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Thank you, <morph.name>. I truly appreciate it.",
                "You are very kind.",
                "This means a lot to me.",
                "Thank you from the heart.",
                "Thanks, <morph.name>. I appreciate it lots. ",
                "I’m grateful.",
                "If you're sharing it with me... thanks."
        ));
        cat.dialoguePool.put("HUMBLE.GIVE_ITEM.FAIL", Arrays.asList(
                "I don't want to waste it.",
                "Someone else might need it more.",
                "Please, keep it.",
                "Maybe another time.",
                "Thank you… but not now.",
                "You should keep it. You might need it more than I do.",
                "I'm fine without it, but thank you.",
                "Save it for someone who needs it more."
        ));

        cat.dialoguePool.put("HUMBLE.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "Thank you… truly.",
                "That brings me peace.",
                "You’re very kind, <morph.name>.",
                "I'm thankful for you.",
                "That warms my heart.",
                "Thank you. I'm glad you're here."
        ));
        cat.dialoguePool.put("HUMBLE.SHOW_AFFECTION.FAIL", Arrays.asList(
                "Maybe another time.",
                "Not now, sorry.",
                "Save that for someone better.",
                "No need to. Thanks anyway, <morph.name>!",
                "I shouldn't accept this.",
                "I appreciate it, but I'm fine."
        ));

        cat.dialoguePool.put("HUMBLE.TALK.SUCCESS", Arrays.asList(
                "I try to do my best… every day. Have I been a good warrior, <morph.name>?",
                "I’m grateful for what I have. A home and friends to rely on.",
                "I don’t need much… just peace and a good friend like you, <morph.name>!",
                "Little moments like this mean a lot.",
                "As long as I’m useful, I’m happy to be here.",
                "I’m grateful to share my days with you and the clan, <morph.name>.",
                "Thank you for thinking of me, <morph.name>.",
                "I'm glad to be part of your clan.",
                "I like simple days. A clean den, a calm clan, and knowing I'm trying my best to be a honorable cat.",
                "When I was younger, I tried so hard to impress others... Now I just try to be useful, to be my best.",
                "I still remember my first successful hunt. It was small scrawny bird… barely enough for one. But I carried it like i just caught a fox."
        ));
        cat.dialoguePool.put("HUMBLE.TALK.FAIL", Arrays.asList(
                "We can talk when I get some time.",
                "Tell me about it another time.",
                "We can leave it for later, okay?",
                "Sorry, not now. I need to think quietly.",
                "Maybe later… if that’s alright.",
                "I don't think I have much to talk right now."
        ));


        cat.dialoguePool.put("RECKLESS.GIVE_ITEM.SUCCESS", Arrays.asList(
                "Nice! Thank you, <morph.name>.",
                "Did you catch this yourself?! Thank you, <morph.name>!",
                "Looks amazing! Thanks, <morph.name>.",
                "Yes! Just what I wanted!",
                "Throw it to me!",
                "Alright. Works for me."
        ));
        cat.dialoguePool.put("RECKLESS.GIVE_ITEM.FAIL", Arrays.asList(
                "Oh come on! I could have caught a better one!",
                "I can help you hunt next time if that's all you could find.",
                "Do you need me to eat this?",
                "Give me something exciting.",
                "I'll pass, thank you anyway.",
                "Nah, I'll manage without it."
        ));

        cat.dialoguePool.put("RECKLESS.SHOW_AFFECTION.SUCCESS", Arrays.asList(
                "Oh! Alright!",
                "Come here!",
                "I appreciate you too, <morph.name>.",
                "Aren't you a sweet smelly furball, <morph.name>?",
                "That's so sweet of you, <morph.name>!"
        ));
        cat.dialoguePool.put("RECKLESS.SHOW_AFFECTION.FAIL", Arrays.asList(
                "Hey, too close.",
                "Don't hold me back!",
                "Do you know about personal space?",
                "You smell like mouse-bile.",
                "We can leave that for later."
        ));

        cat.dialoguePool.put("RECKLESS.TALK.SUCCESS", Arrays.asList(
                "Shall we go hunt?",
                "If a badger shows up, I’m jumping first!",
                "Have you heard the dogs too? I promise I'll teach them a lesson!",
                "Standing still is boring. Could we go hunt?",
                "And trust me, if anything goes wrong... I'll deal with it!",
                "When I was a kit, they always warned me about bees. I never believed them, until one day I decided to bite one... I tell this story to every kit I see now.",
                "Once I chased a squirrel across the river. Next thing I knew was that my fur was already soaked and cold. I’d probably do it again...",
                "One time a fox snapped at me and I snapped back before thinking. My paws moved faster than I could think. That happens a lot to me.",
                "I once jumped over a fallen tree without knowing what was on the other side. Until I found myself stuck in brambles...",
                "When I was young, I climbed a tree higher than I should have. The view was beautiful. Getting down wasn’t so much."
        ));
        cat.dialoguePool.put("RECKLESS.TALK.FAIL", Arrays.asList(
                "Talking? Boring!",
                "I’d rather be fighting or hunting.",
                "Nope. I’m busy being alive.",
                "Save the chatter for latter.",
                "I’m out. Peace.",
                "Sorry, I have better things to do."
        ));


        cat.dialoguePool.put("CALM.GIFT", Arrays.asList(
                "Hey, <morph.name>, I thought you might like this.",
                "<morph.name>! This is for you!",
                "For you, I hope you like it.",
                "Here, this reminded me of you.",
                "Hey, <morph.name>! I got something for you."
        ));
        cat.dialoguePool.put("GRUMPY.GIFT", Arrays.asList(
                "Just don't make a fuss over it.",
                "I thought you might like this.",
                "I don't expect a \"Thank you\".",
                "I hope you appreciate it.",
                "Here, enjoy, you're welcome."
        ));
        cat.dialoguePool.put("CAUTIOUS.GIFT", Arrays.asList(
                "Hey, <morph.name>, this is for you.",
                "I got something for you.",
                "I wasn’t sure if you’d like it, but… here.",
                "Here… I think it might be useful.",
                "If you don’t like it, I can take it back."
        ));
        cat.dialoguePool.put("INDEPENDENT.GIFT", Arrays.asList(
                "I got this myself. Hope you like it.",
                "<morph.name>, This is for you.",
                "I hope you like it.",
                "Here, enjoy.",
                "Sharing is caring, right?"
        ));
        cat.dialoguePool.put("FRIENDLY.GIFT", Arrays.asList(
                "<morph.name>! I got something for you!",
                "Hey, this is for you.",
                "You are a nice friend, you deserve this.",
                "Hey, <morph.name>, I thought you might like this.",
                "I was thinking of giving you this for a while."
        ));
        cat.dialoguePool.put("SHY.GIFT", Arrays.asList(
                "H-hey <morph.name>,  I thought you might like this.",
                "This is for you! Thank you for always being so nice.",
                "I hope this shows how much I appreciate you.",
                "For you!",
                "Um… I hope it’s okay."
        ));
        cat.dialoguePool.put("AMBITIOUS.GIFT", Arrays.asList(
                "Now it's yours, I hope you appreciate it.",
                "Take this, you might need it more than me.",
                "Use it well.",
                "I expect you to make good use of it.",
                "Consider it an investment in you."
        ));
        cat.dialoguePool.put("HUMBLE.GIFT", Arrays.asList(
                "It may not be much, but I tried my best.",
                "This is for you, I hope you like it!",
                "I hope this shows how much I appreciate your friendship.",
                "I told you I would return the favour!",
                "A little gift for a great friend like you."
        ));
        cat.dialoguePool.put("RECKLESS.GIFT", Arrays.asList(
                "Hey, <morph.name>! See if this fits your likes!",
                "You looked a little lost in your thoughts. Maybe this will make you feel better!",
                "Hey, clumsy furball! Catch this!",
                "A little gift for a little furball.",
                "Hey, <morph.name>! Think fast!"
        ));


        cat.dialoguePool.put("TALK.MATE.COMMON.SUCCESS", Arrays.asList(
                "Do you remember the day we met? I keep thinking about how peaceful it felt... just us.",
                "You always know how to make me feel better... I'm glad you're here.",
                "I trust you, <morph.name>. More than anyone else. I'm happy to be with you, I wanted you to know that.",
                "I saw something funny while I was out hunting the other day. I wished you were there so I could tell you about it.",
                "I didn't say it at the time... But when you came back safely that day, I felt so relieved.",
                "<morph.name>! I was waiting for you, any good news?",
                "You can always rely on me, <morph.name>. I'll always be by your side, no matter what.",
                "<morph.name>! How are you? Any good news?",
                "I’ve been thinking about our future lately, <morph.name>. Where do you think we’ll be seasons from now?",
                "Did you know that I love you, <morph.name>? Do not ever forget it.",
                "You have done so much for me, for us... One day I will return the favour.",
                "Hey, <morph.name>! Tell me about your day. I love listening to you.",
                "And remember, if something is ever bothering you, you can tell me. I will always want to carry it with you."
        ));
        cat.dialoguePool.put("TALK.MATE.UNUSUAL.SUCCESS", Arrays.asList(
                "There was a time when I thought I’d never let anyone this close. Then you showed up... and everything changed.",
                "To me, you will always be my beloved dumb furball.",
                "Remember that day I let that squirrel escape? I still think you should have been faster than me, lazy furball!",
                "Hey, <morph.name>! I've missed you, where have you been?",
                "You can always rely on me, <morph.name>. I'll always be by your side, no matter what.",
                "Remember how we didn't get along at first? Who could have guessed...",
                "Sometimes I'm sorry I was so reluctant to be your friend at first, and then I remember you were always an annoying furball, but a really lovely one...",
                "<morph.name>! How are you? Have you eaten? Anything I could help you with?",
                "You stink a little, but I still love you, <morph.name>. I will always do.",
                "I have bad days sometimes... But you will always be who I care the most.",
                "Thank you for staying with me, <morph.name>, even when I’m so complicated.",
                "I still don't understand how you could choose such an annoying cat like me! But... I'm glad you did.",
                "I always love when we go out together. We should do it more often!"
        ));
        cat.dialoguePool.put("TALK.MATE.SHY.SUCCESS", Arrays.asList(
                "<morph.name>! I-I wanted to ask... Do you feel as happy as I feel when you're with me too?",
                "H-hey! I missed you, where have you been? Have you eaten yet?",
                "Sometimes I feel lonely without you... I hope I can always stay by your side.",
                "I like quiet moments… where we don't need to talk at all, but everything still feels... Right.",
                "<morph.name>! Y-you have a little feather on your fur!",
                "If it wasn't for you... I probably would not talk to anyone at all...",
                "And if you ever feel lost... Or lonely like I used to, remember I will always love you, <morph.name>.",
                "O-oh, hey! I knew I could smell something sweet in the air.",
                "Thank you, <morph.name>… for staying by my side, even when I’m awkward with my feelings.",
                "I like being with you… even if we don’t talk much. It feels nice.",
                "I feel braver when you're here… like nothing bad could reach us.",
                "I might not say it too often but... I love you, <morph.name>!",
                "The other day I saw a butterfly, it was beautiful and it looked just like you! I wanted to catch it and save it forever, but then I remembered I already have you.",
                "You can always rely on me, always remember that, you sweet furball."
        ));

    }


    public String getRandomMateDialogue(WCatEntity.Personality personality, InteractionResult result) {

        cat.getDialogueModule().loadDialogueMap();
        String firstKey = "";

        firstKey = switch (personality) {
            case NONE -> "MATE.COMMON.";
            case CALM -> "MATE.COMMON.";
            case GRUMPY -> "MATE.UNUSUAL.";
            case CAUTIOUS -> "MATE.UNUSUAL.";
            case INDEPENDENT -> "MATE.COMMON.";
            case FRIENDLY -> "MATE.COMMON.";
            case SHY -> "MATE.SHY.";
            case AMBITIOUS -> "MATE.COMMON.";
            case HUMBLE -> "MATE.COMMON.";
            case RECKLESS -> "MATE.COMMON.";
        };

        String key = "TALK." + firstKey + result.name();

        List<String> options = cat.dialoguePool.getOrDefault(key, Arrays.asList("..."));

        return options.get(cat.getRandom().nextInt(options.size()));
    }


    public String getRandomDialogue(WCatEntity.Personality personality, WCatEntity.CatInteraction type, InteractionResult result) {

        cat.getDialogueModule().loadDialogueMap();

        String key = personality.name() + "." + type.name() + "." + result.name();

        List<String> options = cat.dialoguePool.getOrDefault(key, Arrays.asList("..."));

        return options.get(cat.getRandom().nextInt(options.size()));
    }

    public String getRandomGiftDialogue(WCatEntity.Personality personality) {
        cat.getDialogueModule().loadDialogueMap();
        String key = personality.name() + ".GIFT";
        List<String> options = cat.dialoguePool.getOrDefault(key, Arrays.asList("..."));
        return options.get(cat.getRandom().nextInt(options.size()));
    }

    public boolean randomInteractionResultProcess(UUID playerUUID, WCatEntity.CatInteraction interaction) {

        if (interaction == WCatEntity.CatInteraction.TALK) {
            if (cat.getMateUUID() != null) {
                if (cat.getMateUUID().equals(playerUUID)) {
                    if (cat.getRandom().nextFloat() <= 0.85 + cat.getMoodInteractionAddition()) {
                        String dialogue = this.getRandomMateDialogue(cat.getPersonality(), InteractionResult.SUCCESS);
                        cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 2);
                        this.sendInteractionMessage(playerUUID, dialogue);
                        cat.randomImproveMood(playerUUID);
                        return true;
                    } else {
                        String dialogue = this.getRandomDialogue(WCatEntity.Personality.CALM, WCatEntity.CatInteraction.TALK, InteractionResult.FAIL);
                        this.sendInteractionMessage(playerUUID, dialogue);
                        return false;
                    }
                }
            }
            if (cat.getPersonality() == WCatEntity.Personality.CALM) {
                if (cat.getRandom().nextFloat() <= 0.7 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.CALM, WCatEntity.CatInteraction.TALK, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);
                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.CALM, WCatEntity.CatInteraction.TALK, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.GRUMPY) {
                if (cat.getRandom().nextFloat() <= 0.4 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.GRUMPY, WCatEntity.CatInteraction.TALK, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 5);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);
                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.GRUMPY, WCatEntity.CatInteraction.TALK, InteractionResult.FAIL);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) - 2);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.CAUTIOUS) {
                if (cat.getRandom().nextFloat() <= 0.7 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.CAUTIOUS, WCatEntity.CatInteraction.TALK, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.CAUTIOUS, WCatEntity.CatInteraction.TALK, InteractionResult.FAIL);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) - 2);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.INDEPENDENT) {
                if (cat.getRandom().nextFloat() <= 0.8 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.INDEPENDENT, WCatEntity.CatInteraction.TALK, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.INDEPENDENT, WCatEntity.CatInteraction.TALK, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.FRIENDLY) {
                if (cat.getRandom().nextFloat() <= 0.8 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.FRIENDLY, WCatEntity.CatInteraction.TALK, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 4);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.FRIENDLY, WCatEntity.CatInteraction.TALK, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.SHY) {
                if (cat.getRandom().nextFloat() <= 0.7 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.SHY, WCatEntity.CatInteraction.TALK, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 3);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.SHY, WCatEntity.CatInteraction.TALK, InteractionResult.FAIL);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) - 1);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.AMBITIOUS) {
                if (cat.getRandom().nextFloat() <= 0.7 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.AMBITIOUS, WCatEntity.CatInteraction.TALK, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.AMBITIOUS, WCatEntity.CatInteraction.TALK, InteractionResult.FAIL);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) - 1);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.HUMBLE) {
                if (cat.getRandom().nextFloat() <= 0.9 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.HUMBLE, WCatEntity.CatInteraction.TALK, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.HUMBLE, WCatEntity.CatInteraction.TALK, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.RECKLESS) {
                if (cat.getRandom().nextFloat() <= 0.7 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.RECKLESS, WCatEntity.CatInteraction.TALK, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.RECKLESS, WCatEntity.CatInteraction.TALK, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            }

        } else if (interaction == WCatEntity.CatInteraction.GIVE_ITEM) {

            if (cat.getPersonality() == WCatEntity.Personality.CALM) {
                if (cat.getRandom().nextFloat() <= 0.7 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.CALM, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 4);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.CALM, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.GRUMPY) {
                if (cat.getRandom().nextFloat() <= 0.45 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.GRUMPY, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 5);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.GRUMPY, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) - 2);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.CAUTIOUS) {
                if (cat.getRandom().nextFloat() <= 0.40 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.CAUTIOUS, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 4);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.CAUTIOUS, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) - 3);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.INDEPENDENT) {
                if (cat.getRandom().nextFloat() <= 0.3 - cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.INDEPENDENT, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.INDEPENDENT, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.FRIENDLY) {
                if (cat.getRandom().nextFloat() <= 0.8 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.FRIENDLY, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 4);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.FRIENDLY, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 1);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.SHY) {
                if (cat.getRandom().nextFloat() <= 0.7 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.SHY, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 5);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.SHY, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.AMBITIOUS) {
                if (cat.getRandom().nextFloat() <= 0.8 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.AMBITIOUS, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);
                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.AMBITIOUS, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) - 3);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.HUMBLE) {
                if (cat.getRandom().nextFloat() <= 0.8 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.HUMBLE, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 5);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.HUMBLE, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.RECKLESS) {
                if (cat.getRandom().nextFloat() <= 0.5 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.RECKLESS, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 3);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.RECKLESS, WCatEntity.CatInteraction.GIVE_ITEM, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            }

        } else if (interaction == WCatEntity.CatInteraction.SHOW_AFFECTION) {


            if (cat.getPersonality() == WCatEntity.Personality.CALM) {
                if (cat.getRandom().nextFloat() <= 0.7 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.CALM, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 3);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.CALM, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.GRUMPY) {
                if (cat.getRandom().nextFloat() <= 0.25 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.GRUMPY, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 7);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.GRUMPY, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) - 3);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.CAUTIOUS) {
                if (cat.getRandom().nextFloat() <= 0.3 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.CAUTIOUS, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 4);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.CAUTIOUS, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) - 2);

                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.INDEPENDENT) {
                if (cat.getRandom().nextFloat() <= 0.6 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.INDEPENDENT, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.INDEPENDENT, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.FRIENDLY) {
                if (cat.getRandom().nextFloat() <= 0.8 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.FRIENDLY, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 6);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.FRIENDLY, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.SHY) {
                if (cat.getRandom().nextFloat() <= 0.8 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.SHY, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.SHY, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.AMBITIOUS) {
                if (cat.getRandom().nextFloat() <= 0.6 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.AMBITIOUS, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 1);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.AMBITIOUS, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.HUMBLE) {
                if (cat.getRandom().nextFloat() <= 0.8 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.HUMBLE, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 3);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.HUMBLE, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            } else if (cat.getPersonality() == WCatEntity.Personality.RECKLESS) {
                if (cat.getRandom().nextFloat() <= 0.6 + cat.getMoodInteractionAddition() + ((double) cat.getFriendshipLevel(playerUUID) / 300)) {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.RECKLESS, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.SUCCESS);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) + 2);
                    this.sendInteractionMessage(playerUUID, dialogue);
                    cat.randomImproveMood(playerUUID);

                    return true;
                } else {
                    String dialogue = this.getRandomDialogue(WCatEntity.Personality.RECKLESS, WCatEntity.CatInteraction.SHOW_AFFECTION, InteractionResult.FAIL);
                    cat.setFriendshipLevel(playerUUID, cat.getFriendshipLevel(playerUUID) - 1);
                    this.sendInteractionMessage(playerUUID, dialogue);

                    return false;
                }
            }

        }

        return false;
    }

    public void sendInteractionMessage(UUID playerUUID, String result) {
        ServerPlayer player = cat.level().getServer().getPlayerList().getPlayer(playerUUID);
        String morphName = player.getCapability(WCEPlayerDataProvider.PLAYER_CLAN_DATA)
                .map(WCEPlayerData::getMorphName).orElse(player.getName().toString());
        String resultCooked = result.replace("<morph.name>", morphName);

        if (player != null) {
            Component name = cat.hasCustomName() ?
                    Component.literal("<").append(cat.getCustomName().copy().withStyle(ChatFormatting.WHITE)).append("> ")
                    :
                    Component.literal("<???> ");
            if (cat.getRank() != KIT && cat.getRank() != APPRENTICE) {
                player.sendSystemMessage(Component.empty().append(name.copy()).append(Component.literal(resultCooked)));
            }
        }
    }
    
}
