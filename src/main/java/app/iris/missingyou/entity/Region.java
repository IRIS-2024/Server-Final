package app.iris.missingyou.entity;

public enum Region {
    //서울
    SEOUL_GANGNAMGU("서울 강남구"),
    SEOUL_GANGDONGGU("서울 강동구"),
    SEOUL_GANGBUKGU("서울 강북구"),
    SEOUL_GANGSEOGU("서울 강서구"),
    SEOUL_GWANGAKGU("서울 관악구"),
    SEOUL_GWANGJINGU("서울 광진구"),
    SEOUL_GUROGU("서울 구로구"),
    SEOUL_GEUMCHEONGU("서울 금천구"),
    SEOUL_NOWONGU("서울 노원구"),
    SEOUL_DOBONGGU("서울 도봉구"),
    SEOUL_DONGDAEMUNGU("서울 동대문구"),
    SEOUL_DONGJAKGU("서울 동작구"),
    SEOUL_MAPOGU("서울 마포구"),
    SEOUL_SEODAEMUNGU("서울 서대문구"),
    SEOUL_SEOCHOGU("서울 서초구"),
    SEOUL_SEONGDONGGU("서울 성동구"),
    SEOUL_SEONGBUKGU("서울 성북구"),
    SEOUL_SONGPAGU("서울 송파구"),
    SEOUL_YANGCHEONGU("서울 양천구"),
    SEOUL_YEONGDEUNGPOGU("서울 영등포구"),
    SEOUL_YONGSANGU("서울 용산구"),
    SEOUL_EUNPYEONGU("서울 은평구"),
    SEOUL_JONGNOGU("서울 종로구"),
    SEOUL_JUNGGU("서울 중구"),
    SEOUL_JUNGLANGGU("서울 중랑구");

    private final String name;

    Region(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
