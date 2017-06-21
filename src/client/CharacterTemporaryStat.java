package client;

import handling.Buffstat;
import java.io.Serializable;

public enum CharacterTemporaryStat implements Serializable, Buffstat {

    IndiePAD(0),
    IndieMAD(1),
    IndiePDD(2), // Physical Defense and Magic defense are now combined, going to keep the name the same since we don't know the actual nexon name
    //IndieMDD(3),
    IndieMHP(3),
    IndieMHPR(4),
    IndieMMP(5),
    IndieMMPR(6),
    IndieACC(7),
    IndieEVA(8),
    IndieJump(9),
    IndieSpeed(10),
    IndieAllStat(11),
    IndieDodgeCriticalTime(12),
    IndieEXP(13),
    IndieBooster(14),
    IndieFixedDamageR(15),
    PyramidStunBuff(16),
    PyramidFrozenBuff(17),
    PyramidFireBuff(18),
    PyramidBonusDamageBuff(19),
    IndieRelaxEXP(20),
    IndieSTR(21),
    IndieDEX(22),
    IndieINT(23),
    IndieLUK(24),
    IndieDamR(25),
    //IndieScriptBuff(25),

    IndieMDF(26),
    IndieMaxDamageOver(27),
    IndieAsrR(28),
    IndieTerR(29),
    IndieCr(30),
    IndiePDDR(31),
    IndieCrMax(32),
    IndieBDR(33),
    IndieStatR(34),
    IndieStance(35),
    IndieIgnoreMobpdpR(36),
    IndieEmpty(37),
    IndiePADR(38),
    IndieMADR(39),
    IndieCrMaxR(40),
    IndieEVAR(41),
    IndieMDDR(42),
    IndieDrainHP(43),
    IndiePMdR(44),
    IndieMaxDamageOverR(45),
    IndieForceJump(46),
    IndieForceSpeed(47),
    IndieQrPointTerm(48),
    // missing 5

    INDIE_STAT_COUNT(53),// alot of buffstats went -3
    PAD(54),
    PDD(55),
    MAD(56),
    MDD(57),
    ACC(58),
    EVA(59),
    Craft(60),
    Speed(61),
    Jump(62),
    MagicGuard(63),
    DarkSight(64),
    Booster(65),
    PowerGuard(66),
    MaxHP(67),
    MaxMP(68),
    Invincible(69),
    SoulArrow(70),
    Stun(71),
    Poison(72),
    Seal(73),
    Darkness(74),
    ComboCounter(75),
    WeaponCharge(76),
    HolySymbol(77),
    MesoUp(78),
    ShadowPartner(79),
    PickPocket(80),
    MesoGuard(81),
    Thaw(82),
    Weakness(83),
    Curse(84),
    Slow(85),
    Morph(86),
    Regen(87),
    BasicStatUp(88),
    Stance(89),
    SharpEyes(90),
    ManaReflection(91),
    Attract(92),
    NoBulletConsume(93),
    Infinity(94),
    AdvancedBless(95),
    IllusionStep(96),
    Blind(97),
    Concentration(98),
    BanMap(99),
    MaxLevelBuff(100),
    MesoUpByItem(101),
    Ghost(102),
    Barrier(103),
    ReverseInput(104),
    ItemUpByItem(105),
    RespectPImmune(106),
    RespectMImmune(107),
    DefenseAtt(108),
    DefenseState(109),
    DojangBerserk(110),
    DojangInvincible(111),
    DojangShield(112),
    SoulMasterFinal(113),
    WindBreakerFinal(114),
    BladeClone(114),
    ElementalReset(115),
    HideAttack(116),
    EventRate(117),
    ComboAbilityBuff(118),
    ComboDrain(119),
    ComboBarrier(120),
    BodyPressure(121),
    RepeatEffect(122),
    ExpBuffRate(123),
    StopPortion(124),
    StopMotion(125),
    Fear(126),
    HiddenPieceOn(127),
    MagicShield(128),
    MagicResistance(129),
    SoulStone(130),
    Flying(131),
    Frozen(132),
    AssistCharge(133),
    Enrage(134),
    DrawBack(135),
    NotDamaged(136),
    FinalCut(137),
    HowlingAttackDamage(138),
    BeastFormDamageUp(139),
    Dance(140),
    EMHP(141),
    EMMP(142),
    EPAD(143),
    EMAD(144),
    EPDD(145),
   // EMDD(146),
    GUARD(146),
    Cyclone(147),
    HowlingCritical(148),
    HowlingMaxMP(149),
    HowlingDefence(150),
    HowlingEvasion(151),
    Conversion(152),
    Revive(153),
    PinkbeanMinibeenMove(154),
    Sneak(155),
    Mechanic(156),
    BeastFormMaxHP(157),
    Dice(158),
    BlessingArmor(159),
    DamR(160),
    TeleportMasteryOn(161),
    CombatOrders(162),
    Beholder(163),
    DispelItemOption(164),
    Inflation(165),
    OnyxDivineProtection(166),
    Web(167),
    Bless(168),
    TimeBomb(169),
    Disorder(170),
    Thread(171),
    Team(172),
    Explosion(173),
    BuffLimit(174),
    STR(175),
    INT(176),
    DEX(177),
    LUK(178),
    DispelItemOptionByField(179),
    DarkTornado(180),
    PVPDamage(181),
    PvPScoreBonus(182),
    PvPInvincible(183),
    PvPRaceEffect(184),
    WeaknessMdamage(185),
    Frozen2(186),
    PVPDamageSkill(187),
    AmplifyDamage(188),
    IceKnight(189),
    Shock(190),
    InfinityForce(191),
    IncMaxHP(192),
    IncMaxMP(193),
    HolyMagicShell(194),
    KeyDownTimeIgnore(195),
    ArcaneAim(196),
    MasterMagicOn(197),
    AsrR(198),
    TerR(199),
    DamAbsorbShield(200),
    DevilishPower(201),
    Roulette(202),
    SpiritLink(203),
    AsrRByItem(204),
    Event(209), // not updated
    CriticalBuff(206),
    //not updated from down on
    DropRate(211),
    PlusExpRate(212),
    ItemInvincible(213),
    Awake(214),
    ItemCritical(215),
    ItemEvade(216),
    Event2(217),
    VampiricTouch(218),
    DDR(219),
    IncCriticalDamMin(220),
    IncCriticalDamMax(221),
    IncTerR(222),
    IncAsrR(223),
    DeathMark(224),
    UsefulAdvancedBless(225),
    Lapidification(226),
    VenomSnake(227),
    CarnivalAttack(228),
    CarnivalDefence(229),
    CarnivalExp(230),
    SlowAttack(231),
    PyramidEffect(232),
    KillingPoint(233),
    HollowPointBullet(234),
    KeyDownMoving(235),
    IgnoreTargetDEF(230), // was 236

    ReviveOnce(237),
    Invisible(238),
    EnrageCr(239),
    EnrageCrDamMin(240),
    Judgement(241),
    DojangLuckyBonus(242),
    PainMark(243),
    Magnet(244),
    MagnetArea(245),
    VampDeath(246),
    BlessingArmorIncPAD(247),
    KeyDownAreaMoving(248),
    Larkness(249),
    StackBuff(250),
    BlessOfDarkness(251),
    AntiMagicShell(252),
    LifeTidal(253),
    HitCriDamR(254),
    SmashStack(255),
    PartyBarrier(256),
    ReshuffleSwitch(257),
    SpecialAction(258),
    VampDeathSummon(259),
    StopForceAtomInfo(260),
    SoulGazeCriDamR(261),
    SoulRageCount(262),
    PowerTransferGauge(263),
    AffinitySlug(264),
    Trinity(265),
    IncMaxDamage(266),
    BossShield(267),
    MobZoneState(268),
    GiveMeHeal(269),
    TouchMe(270),
    Contagion(271),
    ComboUnlimited(272),
    SoulExalt(273),
    IgnorePCounter(274),
    IgnoreAllCounter(275),
    Unknown275(999),
    IgnorePImmune(276),
    IgnoreAllImmune(277),
    FinalJudgement(278),
    IceAura(279),
    FireAura(280),
    VengeanceOfAngel(281),
    HeavensDoor(282),
    Preparation(283),
    BullsEye(284),
    IncEffectHPPotion(285),
    IncEffectMPPotion(286),
    BleedingToxin(287),
    Unknown287(999),
    IgnoreMobDamR(288),
    Asura(289),
    FlipTheCoin(290),
    UnityOfPower(291),
    Stimulate(292),
    ReturnTeleport(293),
    DropRIncrease(294),
    IgnoreMobpdpR(295),
    BDR(296),
    CapDebuff(297),
    Exceed(298),
    DiabolikRecovery(299),
    FinalAttackProp(300),
    ExceedOverload(301),
    OverloadCount(302),
    BuckShot(303),
    FireBomb(304),
    HalfstatByDebuff(305),
    SurplusSupply(306),
    SetBaseDamage(307),
    EVAR(308),
    NewFlying(309),
    AmaranthGenerator(310),
    OnCapsule(311),
    //updated
    CygnusElementSkill(310),
    StrikerHyperElectric(311),
    EventPointAbsorb(312),
    EventAssemble(313),
    StormBringer(314),
    ACCR(315),
    DEXR(316),
    Albatross(317),
    //end updated
    Translucence(320),
    PoseType(321),
    LightOfSpirit(322),
    ElementSoul(323),
    GlimmeringTime(324),
    TrueSight(325),
    SoulExplosion(326),
    SoulMP(327),
    FullSoulMP(328),
    SoulSkillDamageUp(329),
    ElementalCharge(330),
    Restoration(331),
    CrossOverChain(332),
    ChargeBuff(333),
    Reincarnation(334),
    KnightsAura(335),
    ChillingStep(336),
    DotBasedBuff(337),
    BlessEnsenble(338),
    ComboCostInc(339),
    ExtremeArchery(340),
    NaviFlying(341),
    QuiverCatridge(342),
    AdvancedQuiver(343),
    UserControlMob(344),
    ImmuneBarrier(345),
    ArmorPiercing(346),
    ZeroAuraStr(347),
    ZeroAuraSpd(348),
    CriticalGrowing(349),
    QuickDraw(350),
    BowMasterConcentration(351),
    TimeFastABuff(352),
    TimeFastBBuff(353),
    GatherDropR(354),
    AimBox2D(355),
    Unknown355(999),
    IncMonsterBattleCaptureRate(356),
    CursorSniping(357),
    DebuffTolerance(358),
    DotHealHPPerSecond(359),
    SpiritGuard(360),
    PreReviveOnce(361),
    SetBaseDamageByBuff(362),
    LimitMP(363),
    ReflectDamR(364),
    ComboTempest(365),
    MHPCutR(366),
    MMPCutR(367),
    SelfWeakness(368),
    ElementDarkness(369),
    FlareTrick(370),
    Ember(371),
    Dominion(372),
    SiphonVitality(373),
    DarknessAscension(374),
    BossWaitingLinesBuff(375),
    DamageReduce(376),
    ShadowServant(377),
    ShadowIllusion(378),
    KnockBack(379),
    AddAttackCount(380),
    ComplusionSlant(381),
    JaguarSummoned(382),
    JaguarCount(383),
    SSFShootingAttack(384),
    DevilCry(385),
    ShieldAttack(386),
    BMageAura(387),
    DarkLighting(388),
    AttackCountX(389),
    BMageDeath(390),
    BombTime(391),
    NoDebuff(392),
    BattlePvP_Mike_Shield(393),
    BattlePvP_Mike_Bugle(394),
    XenonAegisSystem(395),
    AngelicBursterSoulSeeker(396),
    HiddenPossession(397),
    NightWalkerBat(398),
    NightLordMark(399),
    WizardIgnite(400),
    FireBarrier(401),
    ChangeFoxMan(402),
    BattlePvP_Helena_Mark(403),
    BattlePvP_Helena_WindSpirit(404),
    BattlePvP_LangE_Protection(405),
    BattlePvP_LeeMalNyun_ScaleUp(406),
    BattlePvP_Revive(407),
    PinkbeanAttackBuff(408),
    PinkbeanRelax(409),
    PinkbeanRollingGrade(410),
    PinkbeanYoYoStack(411),
    RandAreaAttack(412),
    NextAttackEnhance(413),
    AranBeyonderDamAbsorb(414),
    AranCombotempastOption(415),
    NautilusFinalAttack(416),
    ViperTimeLeap(417),
    RoyalGuardState(418),
    RoyalGuardPrepare(419),
    MichaelSoulLink(420),
    MichaelStanceLink(421),
    //updated
    TriflingWhimOnOff(424),
    //end updated
    AddRangeOnOff(423),
    KinesisPsychicPoint(424),
    KinesisPsychicOver(425),
    KinesisPsychicShield(426),
    KinesisIncMastery(427),
    KinesisPsychicEnergeShield(428),
    BladeStance(429),
    DebuffActiveSkillHPCon(430),
    DebuffIncHP(431),
    BowMasterMortalBlow(432),
    AngelicBursterSoulResonance(433),
    Fever(434),
    IgnisRore(435),
    RpSiksin(436),
    TeleportMasteryRange(437),
    FixCoolTime(438),
    IncMobRateDummy(439),
    AdrenalinBoost(440),
    AranSmashSwing(441),
    AranDrain(442),
    AranBoostEndHunt(443),
    HiddenHyperLinkMaximization(444),
    RWCylinder(445),
    RWCombination(446),
    RWMagnumBlow(447),
    RWBarrier(448),
    RWBarrierHeal(449),
    RWMaximizeCannon(450),
    RWOverHeat(451),
    UsingScouter(452),
    RWMovingEvar(453),
    Stigma(454),
    // Sengoku/Hayato is 455 -> 473/479
    Unknown240(999),
    Unknown241(999),
    Unknown402(999),
    Unknown403(999),
    Unknown404(999),
    Unknown457(457),
    Unknown462(462),
    Unknown463(463),
    Unknown464(464),
    Unknown465(465),
    Unknown466(466),
    Unknown467(467),
    Unknown468(468),
    Unknown472(472),
    Unknown473(473),
    Unknown474(474),
    Unknown483(483),
    Unknown485(485),
    Unknown496(469),
    Unknown505(505),
    Unknown506(506),
    Unknown510(510),
    Unknown514(514),
    Unknown515(515),
    Unknown516(516),
    Unknown518(518),
    Unknown519(519),
    Unknown520(520),
    //needed buffstats
    AnimalChange(999),
    TeamRoar(999),
    HayatoStance(999),
    HayatoStanceBonus(999),
    HayatoPAD(999),
    HayatoHPR(999),
    HayatoMPR(999),
    HayatoCr(999),
    KannaBDR(999),
    Battoujutsu(999),
    EyeForEye(999),
    FamiliarShadow(999),
    
    
    // Beast Tamer 480 -> 491

    // TwoStateTemporaryStats 492 -> 499
    EnergyCharged(492),
    Dash_Speed(493),
    Dash_Jump(494),
    RideVehicle(495),
    PartyBooster(496),
    GuidedBullet(497),
    Undead(498),
    RideVehicleExpire(499),
    // 500 -> 543 Variety of interesting flags.
    // SoulMasterFinal = DropBuffRate && WindBreakerFinal = BladeClone
    Unknown504(504),
    // TODO:
    SUMMON(74),
    DRAGONBLOOD(76),
    ACASH_RATE(99),
    // 1st
    BEARASSAULT(0x6000, 1),
    PUPPET(0x8000000, 1),
    // 2nd
    BUFF_MASTERY(0x200, 2),
    ILLUSION(0x1000000, 2),
    BERSERK_FURY(0x8000000, 2),
    DIVINE_BODY(0x10000000, 2),
    SPARK(0x20000000, 2),
    ARIANT_COSS_IMU2(0x40000000, 2),
    // 3rd
    WIND_WALK(0x4, 3),
    PYRAMID_PQ(0x200, 3),
    POTION_CURSE(0x800, 3),
    SHADOW(0x1000, 3),
    BLINDNESS(0x2000, 3),
    SLOWNESS(0x4000, 3),
    OWL_SPIRIT(0x400000, 3),
    RAINING_MINES(0x8000000, 3),
    // 4th
    SATELLITESAFE_PROC(0x2, 4),
    SATELLITESAFE_ABSORB(0x4, 4),
    TORNADO(0x20, 4),
    DAMAGE_TAKEN_BUFF(0x100, 4),
    DODGE_CHANGE_BUFF(0x200, 4),
    REAPER(0x800, 4),
    INFILTRATE(0x1000, 4),
    AURA(0x1000, 4),
    DAMAGE_RATE(0x100000, 4),
    ONYX_WILL(0x8000000, 4),
    TORNADO_CURSE(0x40000000, 4),
    // 5th
    THREATEN_PVP(0x4, 5),
    ATTACK(0x100, 5),
    PVP_ATTACK(0x400000, 5),
    INVINCIBILITY(0x800000, 5),
    STACK_ALLSTATS(0x40000, 5),
    SNATCH(0x4000000, 5),
    ICE_SKILL(0x20000000, 5),
    // 6th
    PVP_FLAG(0x2, 6),
    DARK_METAMORPHOSIS(0x80, 6),
    MDEF_BOOST(0x2000, 6),
    WDEF_BOOST(0x4000, 6),
    VIRTUE_EFFECT(0x10000, 6),
    NO_SLIP(0x100000, 6),
    FAMILIAR_SHADOW(0x200000, 6),
    LEECH_AURA(0x2000000, 6),
    ABSORB_DAMAGE_HP(0x20000000, 6),
    DEFENCE_BOOST_R(0x4000000, 6),
    Dusk_Guard(0x2000, 6),
    // 7th
    KILL_COUNT(0x800000, 7),
    // 8th
    PHANTOM_MOVE(0x8, 8),
    HYBRID_DEFENSES(0x400, 8),
    LUMINOUS_GAUGE(0x200, 8),
    KAISER_MODE_CHANGE(0x20000, 8),
    CRIT_DAMAGE(0x200000, 8),
    DEFAULT_BUFFSTAT(0x80000000, 8),
    // 9th
    KAISER_MAJESTY3(0x2, 9),
    KAISER_MAJESTY4(0x4, 9),
    PARTY_STANCE(0x10, 9),
    STATUS_RESIST_TWO(0x10, 9),
    ELEMENT_RESIST_TWO(0x20, 9),
    ANGEL(0x100, 9),
    BOWMASTERHYPER(0x400, 9),
    DAMAGE_RESIST(0x8000, 9),
    MOON_STANCE2(0x80000000, 9),
    EXCEED_ATTACK(0x4000000, 9),
    EXCEED(0x40000000, 9),
    // 10th
    LIGHTNING(0x80, 10),
    STORM_ELEMENTAL(0x80, 10),
    TOUCH_OF_THE_WIND1(0x1000, 10),
    ADD_AVOIDABILITY(0x1000, 10),
    TOUCH_OF_THE_WIND2(0x2000, 10),
    ACCURACY_PERCENT(0x20000, 10),
    WARRIOR_STANCE(0x10000, 10),
    EQUINOX_STANCE(0x80000, 10),
    SOLUNA_EFFECT(0x10000, 10),
    HP_RECOVER(0x4000000, 10),
    CROSS_SURGE(0x8000000, 10),
    PARASHOCK_GUARD(0x80000000, 10),
    // 11th
    PASSIVE_BLESS(0x4, 11),
    DIVINE_FORCE_AURA(0x1000, 11),
    DIVINE_SPEED_AURA(0x2000, 11),
    // 12th
    BATTOUJUTSU_STANCE(0x2, 12),
    HAYATO_STANCE(0x100, 12),
    SHIKIGAMI(0x10, 12),
    HAYATO3(0x20, 12),
    HAYATO4(0x40, 12),
    HAYATO5(0x80, 12),
    FOX_FIRE(0x2000, 12),
    HAKU_REBORN(0x4000, 12),
    HAKU_BLESS(0x8000, 12);

    private static final long serialVersionUID = 0L;
    private final int nValue;
    private final int nPos;
    private final boolean isIndie;

    private CharacterTemporaryStat(int nValue, int nPos) {
        this.nValue = nValue;
        this.nPos = nPos;
        this.isIndie = false;
    }

    private CharacterTemporaryStat(int flag) {
        this.nValue = (1 << (31 - (flag % 32)));
        this.nPos = 17 - (byte) Math.floor(flag / 32);
        this.isIndie = name().contains("Indie");
    }

    @Override
    public int getValue() {
        return nValue;
    }

    @Override
    public int getPosition() {
        return nPos;
    }

    public boolean isIndie() {
        return isIndie;
    }

    public boolean isEnDecode4Byte() {
        switch (this) {
            case RideVehicle:
            case CarnivalDefence:
            case SpiritLink:
            case DojangLuckyBonus:
            case SoulGazeCriDamR:
            case PowerTransferGauge:
            case ReturnTeleport:
            case ShadowPartner:
            case SetBaseDamage:
            case QuiverCatridge:
            case ImmuneBarrier:
            case NaviFlying:
            case Dance:
            case SetBaseDamageByBuff:
            case DotHealHPPerSecond:
            case MagnetArea:
                return true;
            default:
                return false;
        }
    }

    public boolean isMovementAffectingStat() {
        switch (this) {
            case Jump:
            case Stun:
            case Weakness:
            case Slow:
            case Morph:
            case Ghost:
            case BasicStatUp:
            case Attract:
            case Dash_Speed:
            case Dash_Jump:
            case Flying:
            case Frozen:
            case Frozen2:
            case Lapidification:
            case IndieSpeed:
            case IndieJump:
            case KeyDownMoving:
            case EnergyCharged:
            case Mechanic:
            case Magnet:
            case MagnetArea:
            case VampDeath:
            case VampDeathSummon:
            case GiveMeHeal:
            case DarkTornado:
            case NewFlying:
            case NaviFlying:
            case UserControlMob:
            case Dance:
            case SelfWeakness:
            case BattlePvP_Helena_WindSpirit:
            case BattlePvP_LeeMalNyun_ScaleUp:
            case TouchMe:
            case IndieForceSpeed:
            case IndieForceJump:
                return true;
            default:
                return false;
        }
    }

    public static CharacterTemporaryStat getCTSFromTSIndex(int index) {
        switch (index) {
            case 0:
                return EnergyCharged;
            case 1:
                return Dash_Speed;
            case 2:
                return Dash_Jump;
            case 3:
                return RideVehicle;
            case 4:
                return PartyBooster;
            case 5:
                return GuidedBullet;
            case 6:
                return Undead;
            case 7:
                return RideVehicleExpire;
        }
        return null;
    }
}
