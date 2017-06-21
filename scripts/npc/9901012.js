//script by Alcandon

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			cm.sendSimple ("Hey #r#h ##k Im the Custom Map Warper of Maple Blade! #b[Update]#k#rBosses Will Spawn Every 5 Minutes in the Boss Maps (Talk to Spinel)#k \r\n#L2#Skelegon Map No.1\r\n#L3#Skelegon Map No.2\r\n#L15#Skelegon Map No.3\r\n#L16#Skelegon Map No.4\r\n#L17#Skelegon Map No.5\r\n#L21##b[New]#kSkelosaurus Map No.1\r\n#L22##b[New]#kSkelosaurus Map No.2\r\n#L23##b[New]#kSkelosaurus Map No.3\r\n#L24##b[New]#kSkelosaurus Map No.4\r\n#L25##b[New]#kSkelosaurus Map No.5\r\n#L26##b[New]#kSkelosaurus Map No.6\r\n#L4#Level 60 ~ 80 Mobs Mixture No.1\r\n#L5#Level 60 ~ 80 Mobs Mixture No.2\r\n#L6#Mixed Golems No.1\r\n#L7#Mixed Golems No.2\r\n#L8#Newties Golems\r\n#L9#Viking Treasure hunt (Party Training Ground 1)\r\n#L18##b#kWar of Kentaurus (Party Training Ground2)\r\n#L13#Chief Knight's Barrack\r\n#L14#Rise of Retarded Penguins(Level 30+)\r\n#L19##r[HOT FOR PRIEST]#kBuddah's Revenge\r\n#L20##r[HOT FOR PRIEST]#kBuddah's Revenge2");
				} else if (selection == 1) {
				  cm.warp(970010000);
				  cm.dispose();
				} else if (selection == 2) {
				  cm.warp(925100200);
				  cm.dispose();
				} else if (selection == 3) {
				  cm.warp(925100300);
				  cm.dispose();
				} else if (selection == 4) {
				  cm.warp(610030011);
				  cm.dispose();
				} else if (selection == 5) {
				  cm.warp(610030014);
				  cm.dispose();
				} else if (selection == 6) {
				  cm.warp(610030012);
				  cm.dispose();
				} else if (selection == 7) {
				  cm.warp(610030015);
				  cm.dispose();
				} else if (selection == 8) {
				  cm.warp(610030013);
				  cm.dispose();
				} else if (selection == 9) {
				  cm.warp(683000000);
				  cm.dispose();
				} else if (selection == 10) {
				  cm.warp(200080200);
				  cm.dispose();
				} else if (selection == 11) {
				  cm.warp(200080300);
				  cm.dispose();
				} else if (selection == 12) {
				  cm.warp(200080400);
				  cm.dispose();
				} else if (selection == 13) {
				  cm.warp(200080500);
				  cm.dispose();
				} else if (selection == 14) {
				  cm.warp(200080600);
				  cm.dispose();
				} else if (selection == 15) {
				  cm.warp(230040400);
				  cm.dispose();
				} else if (selection == 16) {
				  cm.warp(230040000);
				  cm.dispose();
				} else if (selection == 17) {
				  cm.warp(230040100);
				  cm.dispose();
				} else if (selection == 18) {
				  cm.warp(110020001);
				  cm.dispose();
				} else if (selection == 19) {
				  cm.warp(682000601);
				  cm.dispose();
				} else if (selection == 20) {
				  cm.warp(682000602);
				  cm.dispose();
				} else if (selection == 21) {
				  cm.warp(106020700);
				  cm.dispose();
				} else if (selection == 22) {
				  cm.warp(106020800);
				  cm.dispose();
				} else if (selection == 23) {
				  cm.warp(106021000);
				  cm.dispose();
				} else if (selection == 24) {
				  cm.warp(106021100);
				  cm.dispose();
				} else if (selection == 25) {
				  cm.warp(106021200);
				  cm.dispose();
				} else if (selection == 26) {
				  cm.warp(106021300);
				  cm.dispose();
			    }
}
}