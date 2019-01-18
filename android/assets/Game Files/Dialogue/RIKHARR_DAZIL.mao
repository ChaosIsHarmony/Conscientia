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
        TRU:Famlicus shows up w/ well wishes and a desire to discuss the Muninn
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
		*CALLBACK to the description in the Book of Eidos, except they are open already and only close at nighttime.
		
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
FIRST PHASE - CHAOS STIRS ~10.xxx~
______________
KHARR MANOR
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

        WELLSPRING Artisan comes in with urgent news about the Cage.*
		(A#...){A:0,KAVU!DAZIL!KHARR MANOR!10.300!DESCRIPTION!}
	[KAVU!DAZIL!KHARR MANOR!10.100!DESCRIPTION!/]

    [/KAVU!DAZIL!KHARR MANOR!10.200!DESCRIPTION!]
		||
		*TALK to Famlicus about:
            DIP: Himself and Muninns
            TRU: Muninns and Hel (tries to convince Rik it's worth investigating)
            NEU: Just Muninns

        WELLSPRING Artisan comes in with urgent news about the Cage.*
		(A#...){A:0,KAVU!DAZIL!KHARR MANOR!10.300!DESCRIPTION!}
	[KAVU!DAZIL!KHARR MANOR!10.200!DESCRIPTION!/]

    [/KAVU!DAZIL!KHARR MANOR!10.300!DESCRIPTION!]
		||
		*WANT to relax after ceremony.

        GET briefed on situation in the Wellspring. Then head on over immediately.*
		(A#...){A:0,KAVU!DAZIL!KHARR MANOR!10.300!DESCRIPTION!}
	[KAVU!DAZIL!KHARR MANOR!10.300!DESCRIPTION!/]

    [/KAVU!DAZIL!KHARR MANOR!10.400!DESCRIPTION!]
		||
		*RADYSAR comes with her entourage to congratulate Rikharr on his Ascension and to propose a trip to Tacriva to convince the High Mage there to give saplings to the Archives of Tambul and Dazil.

        TRUSTED servant tells Rikharr when she leaves that his father met with her an awful lot before his untimely demise and that he should be weary of her intentions.

        WELLSPRING Artisan comes in with urgent news about the Cage.*
		(A#...){A:0,KAVU!DAZIL!KHARR MANOR!10.400!DESCRIPTION!}
	[KAVU!DAZIL!KHARR MANOR!10.400!DESCRIPTION!/]

    [/KAVU!DAZIL!KHARR MANOR!10.500!DESCRIPTION!]
		||
		*SUN TOWER messenger invokes the Guardians' Pact and has come to tell Rikharr of the impending assault of the wasteland draugs, who have grown too numerous from hundreds of years of exile and have become too aggressive in the past few weeks, with several groups raiding at the same time. Since their still leaderless idiots, the Guardians can handle it, but if they attacked en masse, they might be overrun. As the youngest mage, he is to oversee report there within two days time

        WELLSPRING Artisan comes in with urgent news about the Cage.*
		(A#...){A:0,KAVU!DAZIL!KHARR MANOR!10.500!DESCRIPTION!}
	[KAVU!DAZIL!KHARR MANOR!10.500!DESCRIPTION!/]

    [/KAVU!DAZIL!KHARR MANOR!10.600!DESCRIPTION!]
		||
		*ARK wants to bone.

        WELLSPRING Artisan comes in with urgent news about the Cage.*
		(A#...){A:0,KAVU!DAZIL!KHARR MANOR!10.600!DESCRIPTION!}
	[KAVU!DAZIL!KHARR MANOR!10.600!DESCRIPTION!/]




________________________________________________________________________
SECOND PHASE - CHAOS SEETHES ~20.xxx~




________________________________________________________________________
FINAL PHASE - CHAOS SWELLS ~30.xxx~




________________________________________________________________________
EVENT WRITERS
________________________________________________________________________
NPC SWITCHERS
________________________________________________________________________
FIGHTING WORDS
