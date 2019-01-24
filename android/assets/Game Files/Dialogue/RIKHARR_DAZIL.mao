________________________________________________________________________
PROLOGUE ~0.xxx~
______________
ARBORETUM
[/KAVU!DAZIL!ARBORETUM!0.X000!DESCRIPTION!]
		||
		*EXPLORE one of six choices (someone comes to find him as he's about to leave), depending on personality affinity and eventually end up back in Kharr Manor.*
		(A#Return to Kharr Manor after exploration){A:0,KAVU!DAZIL!KHARR MANOR!0.X000!DESCRIPTION!}
	[KAVU!DAZIL!ARBORETUM!0.X000!DESCRIPTION!/]




______________
ATRIUM
	[/KAVU!DAZIL!ATRIUM!0.X000!DESCRIPTION!]
		||
		*COMMOTION seems to be coming from a group of people, can ask the courier what it is about or ignore it - it is a Builders' rally about improving their subservient role in society; really, they are basically a caste with zero social mobility and a huge stigma attached to them; never allowed to leave Dazir except to perform dangerous repairs in the Wellspring or on the Aqueducts.*
		(A#Go to Kharr Manor){A:0,KAVU!DAZIL!KHARR MANOR!0.X000!DESCRIPTION!}
	[KAVU!DAZIL!ATRIUM!0.X000!DESCRIPTION!/]

	[/KAVU!DAZIL!ATRIUM!9.000!DESCRIPTION!]
		||
		*ASCENDENCE Ceremony occurs with one of six events the player selects (actual choice of three highest affinities).
        
        DIP:The Builders approach Rik to talk about social change
        TRU:Lazham shows up w/ well wishes and a desire to discuss the Muninn
        NEU:Go back to Manor, where an Artisan shows up with important new about the completion of reparations of the Cage in the Wellspring
        SUR:Radysar, Mage of Tambul, involves Rik in talks to get Muninn saplings for Tambul and Dazil
        TYR:Word from the Sun Tower arrives; Rikharr will receive the messenger at the Manor.
        LOO:Ark says 'Hellooooo, Rik!'*
		(A#Go to Kharr Manor){A:0,KAVU!DAZIL!KHARR MANOR!10.X000!DESCRIPTION!}
	[KAVU!DAZIL!ATRIUM!9.000!DESCRIPTION!/]



______________
GATES OF DAZIL
	[/KAVU!DAZIL!GATES OF DAZIR!1.X000!DESCRIPTION!]
		|MUNINN STUFF:KAVU!DAZIL!GATES OF DAZIR!1.010!DESCRIPTION!|
		*CALLBACK to the description in the Book of Eidos, except they are open already and only close at nighttime. Also, there are Neverborn sentries guarding the place and stationed all over Kavu as gaurds, cleaners, pack bearers, etc.
		
		HEAR a commotion at the far end of the Atrium.*
		(A#...){A:0,KAVU!DAZIL!ATRIUM!0.X000!DESCRIPTION!}
	[KAVU!DAZIL!GATES OF DAZIR!1.X000!DESCRIPTION!/]

	[/KAVU!DAZIL!GATES OF DAZIR!1.010!DESCRIPTION!]
		||
		*CALLBACK to the description in the Book of Eidos, except they are open already and only close at nighttime.
		
        PEOPLE inquire about Rikos' collapse.

		HEAR a commotion at the far end of the Atrium.*
		(A#...){A:0,KAVU!DAZIL!ATRIUM!0.X000!DESCRIPTION!}
	[KAVU!DAZIL!GATES OF DAZIR!1.010!DESCRIPTION!/]




______________
KHARR MANOR
	[/KAVU!DAZIL!KHARR MANOR!0.X000!DESCRIPTION!]
		||
		*DISCUSS details of death. Have other people mention how Rikos will now ascend to Rikharr. The ceremony will happen tomorrow.*
		(A#Explore Dazil){A:0,KAVU!DAZIL!ARBORETUM!0.X000!DESCRIPTION!}
		(B#Go to sleep){B:0,KAVU!DAZIL!KHARR MANOR!9.000!DESCRIPTION!}
	[KAVU!DAZIL!KHARR MANOR!0.X000!DESCRIPTION!/]
	
	[/KAVU!DAZIL!KHARR MANOR!9.000!DESCRIPTION!]
		||
		*RIKOS enters a dreamless sleep and awakens the next day where he must attend the Ascendance Ceremony.*
		(A#Explore Dazil){A:0,KAVU!DAZIL!ATRIUM!9.000!DESCRIPTION!}
	[KAVU!DAZIL!KHARR MANOR!9.000!DESCRIPTION!/]



________________________________________________________________________
________________________________________________________________________
FIRST PHASE - CHAOS STIRS ~10.xxx~
______________
KHARR MANOR
>>>Check for which storyline is active<<<
	[/KAVU!DAZIL!KHARR MANOR!10.X000!DESCRIPTION!]
		||
		*MULTICHECKER*
		(A#CHECKER){A:0,NO ADDRESS}
	[KAVU!DAZIL!KHARR MANOR!10.X000!DESCRIPTION!/]



    [/KAVU!DAZIL!KHARR MANOR!10.100!DESCRIPTION!]
		||
		*MEET with Builders, who are:

            DIP: Looking to negotiate terms
            TRU: Trying to debate with Rik about the wrongness of their current situation
            TYR: Threatening violence if Rik continues the Kharr way of old

        WELLSPRING Artisan comes in with urgent news about his father's funeral rites.*
		(A#...){A:0,KAVU!DAZIL!KHARR MANOR!10.300!DESCRIPTION!}
	[KAVU!DAZIL!KHARR MANOR!10.100!DESCRIPTION!/]

    [/KAVU!DAZIL!KHARR MANOR!10.200!DESCRIPTION!]
		||
		*TALK to Lazham about:

            DIP: Himself and Muninns
            TRU: Muninns and Hel (tries to convince Rik it's worth investigating)
            NEU: Just Muninns

        WELLSPRING Artisan comes in with urgent news about his father's funeral rites.*
		(A#...){A:0,KAVU!DAZIL!KHARR MANOR!10.300!DESCRIPTION!}
	[KAVU!DAZIL!KHARR MANOR!10.200!DESCRIPTION!/]

    [/KAVU!DAZIL!KHARR MANOR!10.300!DESCRIPTION!]
		||
		*WANT to relax after ceremony...

            TRU: Trusted servant mentions the foreign mage who was in attendance.
            NEU: Rikos' eyes close. He knows not how long he sleeps.
            SUR: Trusted servant claims Tambulans are all talking about the possibility of receiving a Muninn sapling.

        GET briefed on situation in the Wellspring (about his father's funeral rites). Then head on over immediately.*
		(A#...){A:0,KAVU!WELLSPRING!GATE OF THE HEATHEN!10.000!DESCRIPTION!}
	[KAVU!DAZIL!KHARR MANOR!10.300!DESCRIPTION!/]

    [/KAVU!DAZIL!KHARR MANOR!10.400!DESCRIPTION!]
		||
		*RADYSAR comes with her entourage to congratulate Rikharr on his Ascension and to propose a trip to Tacriva to convince the High Mage there to give saplings to the Archives of Tambul and Dazil.

            NEU: Merely mentions saplings and trip to Tacriva
            SUR: Muninns + Suggests a foreign alchemist might have had something to do with Drakos' death
            TYR: Muninns + Complains about draug issues

        TRUSTED servant tells Rikharr when she leaves that his father met with her an awful lot before his untimely demise and that he should be weary of her intentions.

        WELLSPRING Artisan comes in with urgent news about his father's funeral rites.*
		(A#...){A:0,KAVU!DAZIL!KHARR MANOR!10.400!DESCRIPTION!}
	[KAVU!DAZIL!KHARR MANOR!10.400!DESCRIPTION!/]

    [/KAVU!DAZIL!KHARR MANOR!10.500!DESCRIPTION!]
		||
		*SUN TOWER messenger invokes the Guardians' Pact and has come to tell Rikharr of the impending assault of the wasteland draugs, who have grown too numerous from hundreds of years of exile and have become too aggressive in the past few weeks, with several groups raiding at the same time. The High Council of Magi in Tacriva has chosen Dazil {the winds blow from the Northwest, signalling Dazil} to oversee operations in the Wasteland, and must report there within two days time.

        WELLSPRING Artisan comes in with urgent news about his father's funeral rites.*
		(A#...){A:0,KAVU!DAZIL!KHARR MANOR!10.500!DESCRIPTION!}
	[KAVU!DAZIL!KHARR MANOR!10.500!DESCRIPTION!/]

    [/KAVU!DAZIL!KHARR MANOR!10.600!DESCRIPTION!]
		||
		*ARK wants to bone. Agree to go for a stroll on the Jenowin Plain.

            DIP: Witness some violence with the Neverborn sentries and the Builders near the cloudstone when coming back.
            TRU: See Lazham strolling through the desert on the way to Tambul.
            NEU: Everything is oddly quiet.
            SUR: Meet Radysar's entourage en route to Tacriva.
            TYR: Receive news of draug attacks from a courier on the road to Tambul with a decree from the High Council that Radysar must deal with Overseeing the operation.
            LOO: Accidentally eat a mushroom {plucked it instead of grabbing the bread that was on the ground because was trying to listen to Ark}. See a vision of Roostor.
        
        WELLSPRING Artisan comes in with urgent news about his father's funeral rites.*
		(A#...){A:0,KAVU!DAZIL!KHARR MANOR!10.600!DESCRIPTION!}
	[KAVU!DAZIL!KHARR MANOR!10.600!DESCRIPTION!/]


back from the Wellspring
    [/KAVU!DAZIL!KHARR MANOR!19.000!DESCRIPTION!]
		||
		*BACK from Wellspring. Woke up three days later. Whichever quest is active will now continue.

            DIP: Builders
            TRU: Muninn
            NEU: Straight to the Festival of Light
            SUR: The Muninn sapling arrives and has been planted in the Archives by Archivists
            TYR: The draug seem to be gathering, but not yet attacking
            LOO: Ark still hasn't woken up. Apparently, she managed to drag both of your asses back to the cage and use the Sigil of Wulfias, but then collapsed from exhaustion

        TRUSTED servant who was taking care of you mentions that the Festival of Light is coming soon. Shares more plot development for one random background plot point.*
		(A#To the Slums){A:0,KAVU!DAZIL!SLUMS!20.000!DESCRIPTION!}
        (B#To the Muninn){B:0,KAVU!TACRIVA!ARCHIVES!20.000!DESCRIPTION!}
        (C#...){C:0,KAVU!DAZIL!KHARR MANOR!20.000!DESCRIPTION!}
        (D#To the Archives){D:0,KAVU!DAZIL!ARCHIVES!20.000!DESCRIPTION!}
        (E#To the Sun Tower){E:0,KAVU!TAMBUL!DYSAR MANOR!20.000!DESCRIPTION!}
        (F#To watch over Ark){F:0,KAVU!DAZIL!KHARR MANOR!20.000!DESCRIPTION!}
	[KAVU!DAZIL!KHARR MANOR!19.000!DESCRIPTION!/]





________________________________________________________________________
________________________________________________________________________
SECOND PHASE - CHAOS SEETHES ~20.xxx~
{No matter what, the sapling arrives before the festival, the draugnir made it slightly past and something happens with the Builders}
______________
ARCHIVES




______________
KHARR MANOR
{Ark wakes up and then you go to the Dining Room in the Artisans' Plaza, but she asks if she can take you to the one in the Slums, because it's more fun. You go and have fun}

{While in Wasteland, riot happened with the Builders at home; Neverborn sentries slain, as well as scores of Builders}

back from the Wasteland
    [/KAVU!DAZIL!KHARR MANOR!29.000!DESCRIPTION!]
		||
		*BACK from Wasteland.

            DIP: Builders
            TRU: Muninn
            NEU: Straight to the Festival of Light
            SUR: The Muninn sapling arrives and has been planted in the Archives by Archivists
            TYR: The draug seem to be gathering, but not yet attacking
            LOO: Ark still hasn't woken up. Apparently, she managed to drag both of your asses back to the cage and use the Sigil of Wulfias, but then collapsed from exhaustion

        TRUSTED servant who was taking care of you mentions that the Festival of Light is coming soon. Shares more plot development for one random background plot point.*
		(A#...){A:0,KAVU!DAZIL!SLUMS!20.000!DESCRIPTION!}
        (B#...){B:0,KAVU!DAZIL!ATRIUM!20.000!DESCRIPTION!}
        (C#...){C:0,KAVU!DAZIL!KHARR MANOR!20.000!DESCRIPTION!}
        (D#...){D:0,KAVU!DAZIL!ARCHIVES!20.000!DESCRIPTION!}
        (E#...){E:0,KAVU!TAMBUL!DYSAR MANOR!20.000!DESCRIPTION!}
        (F#...){F:0,KAVU!DAZIL!KHARR MANOR!20.000!DESCRIPTION!}
	[KAVU!DAZIL!KHARR MANOR!29.000!DESCRIPTION!/]


______________
SLUMS
	 [/KAVU!DAZIL!SLUMS!20.000!DESCRIPTION!]
		||
		*SETTLE the Builders' issues.
		
			DIP: Reforms will end branding and increase social mobility for Builders who prove themselves capable
            TRU: Public debate with Builders in attendance; a loss means negotiation, a win means a riot
            TYR: Rik suggests a public discussion, but they begin to riot and violence ensues.
			
		*
		(A#...){A:0,KAVU!DAZIL!SLUMS!20.000!DESCRIPTION!}
	[KAVU!DAZIL!SLUMS!20.000!DESCRIPTION!/]


________________________________________________________________________
________________________________________________________________________
________________________________________________________________________
EVENT WRITERS
________________________________________________________________________
NPC SWITCHERS
________________________________________________________________________
FIGHTING WORDS
