package com.ssafy.a603.lingoland.member.entity;

public enum Rank {

    CHAMBONG("종9품: 참봉", 1, 150),
    HUNDO("정9품: 훈도", 2, 174),
    JEONGJA("정9품: 정자", 3, 202),
    BUBONGSA("정9품: 부봉사", 4, 234),
    BONGSA("종8품: 봉사", 5, 271),
    JEOJAK("정8품: 저작", 6, 314),
    JIKJANG("종7품: 직장", 7, 364),
    BAKSA("정7품: 박사", 8, 422),
    GYOSU("종6품: 교수", 9, 489),
    JUBU("종6품: 주부", 10, 566),
    BYEOLJE("정6품: 별제", 11, 656),
    JWARANG("정6품: 좌랑", 12, 759),
    PANGWAN("종5품: 판관", 13, 879),
    DOSA("종5품: 도사", 14, 1019),
    GYORI("정5품: 교리", 15, 1181),
    BYEOLJWA("정5품: 별좌", 16, 1368),
    JEONGLANG("정5품: 정랑", 17, 1585),
    CHUMJEONG("종4품: 첨정", 18, 1837),
    KYUNGRYEOK("종4품: 경력", 19, 2130),
    JANGRYEONG("정4품: 장령", 20, 2470),
    SAIN("정4품: 사인", 21, 2862),
    SAGAN("종3품: 사간", 22, 3318),
    JIBUI("종3품: 집의", 23, 3847),
    JIKJAEHAK("정3품: 직제학", 24, 4458),
    CHAMUI("정3품: 참의", 25, 5160),
    SANGSEON("종2품: 상선", 26, 5967),
    CHAMPAN("종2품: 참판", 27, 6898),
    DONGJISA("종2품: 동지사", 28, 7974),
    DAEJAEHAK("정2품: 대제학", 29, 9223),
    WOOCHAMCHAN("정2품: 우참찬", 30, 10687),
    JOICHAMCHAN("정2품: 좌참찬", 31, 12392),
    PANSEO("정2품: 판서", 32, 14376),
    JISA("정2품: 지사", 33, 16688),
    JAEJO("종1품: 제조", 34, 19355),
    PANSA("종1품: 판사", 35, 22457),
    WOCHANSEONG("종1품: 우찬성", 36, 26016),
    JWACHANSEONG("종1품: 좌찬성", 37, 30144),
    DOJAEJO("정1품: 도제조", 38, 34915),
    WOOUIJEONG("정1품: 우의정", 39, 40512),
    JWAUIJEONG("정1품: 좌의정", 40, 46993),
    YEONGUIJEONG("정1품: 영의정", 41, 54492);

    private final String grade;
    private final int order;
    private final long maxExperience;

    Rank(String grade, int order, long maxExperience) {
        this.grade = grade;
        this.order = order;
        this.maxExperience = maxExperience;
    }

    public String getGrade() {
        return grade;
    }

    public int getOrder() {
        return order;
    }

    public long getMaxExperience() {
        return maxExperience;
    }

    public Rank nextRank() {
        int nextOrder = this.order + 1;
        for (Rank rank : Rank.values()) {
            if (rank.getOrder() == nextOrder) {
                return rank;
            }
        }
        return this;
    }
}
