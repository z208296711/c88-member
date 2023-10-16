package com.c88.member.constants;

public class RedisKey {



    private RedisKey() {
        throw new IllegalStateException("Utility class");
    }

    public static final String UPDATE_BALANCE_WITHDRAW_LIMIT = "updateBalanceAndWithdrawLimit";
    public static final String ADD_BALANCE = "addBalance";
    public static final String MEMBER_DAILY_MAX_TOTAL = "memberDailyMaxTotal";
    public static final String MEMBER_MAX_TOTAL = "memberMaxTotal";
    public static final String DRAW_ACTION = "drawAction";
    public static final String MEMBER_RED_ENVELOPE_TEMPLATE_LEVEL = "memberRedEnvelopeTemplateLevel";
    public static final String MEMBER_RED_ENVELOPE_TEMPLATE_CYCLE = "memberRedEnvelopeTemplateCycle";
    public static final String RED_ENVELOPE_NUMBER = "redEnvelopeNumber";
    public static final String RED_ENVELOPE_DAILY_NUMBER = "redEnvelopeDailyNumber";
    public static final String MEMBER_RED_ENVELOPE_NUMBER = "memberRedEnvelopeNumber";
    public static final String MEMBER_RED_ENVELOPE_DAILY_NUMBER = "memberRedEnvelopeDailyNumber";
    public static final String MEMBER_TEMPLATE_RECEIVE = "memberTemplateReceive";


    public static final String MEMBER_DRAW_RESET_TIME = "memberDrawResetTime";
    public static final String MEMBER_DRAW_PRECONDITION_START_TIME = "memberDrawPreconditionStartTime";
    public static final String MEMBER_TODAY_DRAW = "memberTodayDraw";
    public static final String MEMBER_TODAY_DRAW_COUNT = "memberTodayDrawCount";
    public static final String MEMBER_TODAY_FREE_DRAW_COUNT = "memberTodayFreeDrawCount";

}
