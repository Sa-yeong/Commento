# 1. 월별 접속자 수
## 리턴 타입 객체 : MonthlyVisitorDto
```
private String yearMonth;
private int count;

public MonthlyVisitorDto(String yearMonth, int count){
    this.yearMonth = yearMonth;
    this.count = count;
}

public String getYearMonth(){return yearMonth;}
public int getCount(){return count;}

public void setYearMonth(String yearMonth){ this.yearMonth = yearMonth;}
public void setCount(int count){ this.count = count;}
```
## Mapper.xml
```
<select id='selectMonthlyVisitor" parameterType="string" resultType="MonthlyVisitorDto">
    select concat('20',#{year}, '-' temp.month) as yearMonth, Count(*) as count
    from (select substr(ri.create_date, 3, 2) as month
          from statistic.request_info ri
          where left(ri.create_date,2) = #{year}) temp
    group by temp.month
</select>
```
## 결과
<img width="2559" height="484" alt="image" src="https://github.com/user-attachments/assets/90013e33-bd18-46a9-b05c-56b0b88728c3" />

# 2. 일자별 접속자 수 
## return Type : DailyVisitorDto
```
private String date;
    private int count;

    public DailyVisitorDto(String date, int count) {
        this.date = date;
        this.count = count;
    }

    public String getDate() { return date; }
    public int getCount() { return count; }

    public void setDate(String date) { this.date = date; }
    public void setCount(int count) { this.count = count; }
```

## Mapper.xml
```
<select id="selectDailyVisitors" parameterType="string" resultType="DailyVisitorDto">
        select concat('20',left(#{yearMonth}, 2), '-', right(#{yearMonth}, 2), '-', temp.day) as date, count(*) as count
        from (select substr(ri.create_date, 5, 2) as day
            from statistic.request_info ri
            where left(ri.create_date, 4) = #{yearMonth}) temp
        group by temp.day;
</select>
```
## 결과
<img width="1323" height="375" alt="image" src="https://github.com/user-attachments/assets/d33a2ec3-e8a6-4b53-b105-eff3a28c2e60" />

# 3. 평균 하루 로그인 수
## Return type: DailyAvgDto
```
private String yearMonth;
    private Float avgCount;

    public DailyAvgDto(String yearMonth, Float avgCount) {
        this.yearMonth = yearMonth;
        this.avgCount = avgCount;
    }

    public String getYearMonth() { return yearMonth; }
    public Float getAvgCount() { return avgCount; }

    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }
    public void setAvgCount(Float avgCount) { this.avgCount = avgCount; }
```

## Mapper.xml
```
<select id="selectDailyAvgLogins" parameterType="string" resultType="DailyAvgDto">
        select concat('20', #{year}, '-', temp.month) as yearMonth, temp.tot/30 as avgCount
        from (select substr(ri.create_date, 3, 2) as month, count(*) as tot
            from request_info ri
            where left(ri.create_date, 2) = #{year} and ri.request_code = 'L'
            group by substr(ri.create_date,3, 2)) temp;
</select>
```
# 결과
<img width="1488" height="359" alt="image" src="https://github.com/user-attachments/assets/62be0b66-5410-4bf7-bb26-5ebcf0ab7a50" />

# 4. 휴일을 제외한 로그인 수
공공API를 이용해 공휴일 정보를 가져와서 활용
## Return type: 1번 문제와 동일
## HoliService.java
```
@Service
public class HoliService {
    @Autowired
    StatisticMapper statisticMapper;

    public List<MonthlyVisitorDto> getHolidayData(String year){
        String url = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo?";
        url += "ServiceKey=서비스키&_type=json";
        url += "&solYear=20" + year;

        RestTemplate restTemplate = new RestTemplate();
        HoliApiDto holiApi = restTemplate.getForObject(url,HoliApiDto.class);

        List<String> resultLi = new ArrayList<>();

        if(holiApi!=null && holiApi.getResponse()!=null && holiApi.getResponse().getBody() !=null && holiApi.getResponse().getBody().getItems()!=null){
            for(HoliApiDto.Item item : holiApi.getResponse().getBody().getItems().getitem()){
                resultLi.add(item.getLocdate());
            }
        }
        return statisticMapper.selectExcludingHoliLogins(year, resultLi);
    }
}
```
## Mapper.xml
```
<select id="selectExcludingHoliLogins" resultType="MonthlyVisitorDto">
        select concat('20', #{year}, '-', temp.month) as yearMonth, Count(*) as count
        from (select substr(ri.create_date, 3, 2) as month, right(ri.create_date,2) as day
            from statistic.request_info ri
            where left(ri.create_date,2) = #{year} and ri.request_code = 'L') temp
        <where>
            <if test="holidays != null and holidays.size > 0">
                concat('20',#{year},temp.month, temp.day) NOT IN
                <foreach collection="holidays" item="date" open="(" separator="," close=")">
                    #{date}
                </foreach>
            </if>
        </where>
        group by temp.month;
</select>
```

## 결과
<img width="1264" height="421" alt="image" src="https://github.com/user-attachments/assets/ccae4382-6f63-456e-82d8-67a07b7fcd64" />

# 5. 부서별 월별 로그인 수
## Return type : MontlyDepDto
```
private String yearMonth;
    private String dep;
    private int count;

    public MonthlyDepDto(String dep, String yearMonth, int count) {
        this.yearMonth = yearMonth;
        this.dep = dep;
        this.count = count;
    }

    public String getDep() { return dep; }
    public String getYearMonth() { return yearMonth; }
    public int getCount() { return count; }

    public void setDep(String dep) { this.dep = dep; }
    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }
    public void setCount(int count) { this.count = count; }
```

## Mapper.xml
```
<select id="selectMonthlyDepLogin" parameterType="String" resultType="MonthlyDepDto">
        select concat('20', #{year}, '-', temp.month) as yearMonth, temp.dep as dep, count(*) as count
        from (select substr(ri.create_date, 3, 2) as month, u.hr_organ as dep
            from statistic.request_info ri, statistic.user u
            where left(ri.create_date, 2) = #{year} and ri.user_id = u.user_id and ri.request_code = 'L') as temp
        group by temp.month, temp.dep
        order by temp.month asc;
</select>
```
## 결과
<img width="2000" height="333" alt="image" src="https://github.com/user-attachments/assets/afc19bd9-1c42-42bb-a9d9-d1c8520457e7" />
