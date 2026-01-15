## 1. 간단한 API 만들기
### 1) 특정 년도의 접속자 수 
  - 결과 화면
<img width="975" height="377" alt="image" src="https://github.com/user-attachments/assets/8750424c-474a-4724-be1d-2ab4877c9eb1" />

### 2) 특정 년도와 월의 접속자 수
- 결과 화면
<img width="1066" height="382" alt="image" src="https://github.com/user-attachments/assets/767d04df-c69b-4742-b706-be23ba9cc844" />

### 3) 위의 결과를 위해 고친 파일
#### a) com.demo.comentoStatistic.dto.YearCountDto.java
```
public class YearCountDto{
  private Strig year;
  private int totCnt;

  public YearCountDto(String year, int totCnt){
    this.year = year;
    this.totCnt = totCnt;
  }

  public String getYear(){
    return year;
  }
  public int getTotCnt(){
    return totCnt;
  }

  public void setYear(Stirn year){
    this.year = year;
  }
  public void setTotCnt(int totCnt){
    this.totCnt = totCnt;
  }
}
```
#### b) com.demo.comentoStatistic.dto.YearMonthCountDto.java
```
public class YearMonthCountDto{
  private String yearMonth;
  private int totCnt;

  public YearMonthCountDto(String yearMonth, int totCnt){
    this.yearMonth = yearMonth;
    this.totCnt = totCnt;
  }

  public String getYearMonth(){
    return yearMonth;
  }
  public int getTotCnt(){
    return totCnt;
  }

  public void setYearMonth(String yearMonth){
    this.yearMonth = yearMonth;
  }
  public void setTotCnt(int totCnt){
    this.totCnt = totCnt;
  }
}
```

## 2. 4주차 과제에서 만들 API SQL문 작성
### 1) 월별 접속자 수
```
select temp.month, Count(*)
from(select substr(ri.create_date, 3, 2) as month
    from statistic.request_info ri
    where left(ri.create_date, 2) = 20) temp
group by temp.month;
```
- Dbeaver에서 실행 시 나온 결과
<img width="403" height="270" alt="image" src="https://github.com/user-attachments/assets/e6409bad-50e6-4dfc-8676-0793a8250f4e" />

### 2) 일자별 접속자 수
```
select temp.day, Count(*) as num
from (select substr(ri.create_date, 5, 2) as day
      from statistic.request_info ri
      where left(ri.create_date, 4) = 2004) temp
group by temp.day;
```
- 결과
<img width="343" height="110" alt="image" src="https://github.com/user-attachments/assets/b083bd85-ed43-4d1c-bdbc-50793e4439f5" />

### 3) 평균 하루 로그인 수
```
select temp.month, temp.tot/30
from (select substr(ri.create_date, 3, 2) as month, Count(*) as tot
      from request_info ri
      where left(ri.create_date, 2) = 20 and ri.request_code = 'L'
      group by substr(ri.create_date, 3, 2))as temp;
```
- 결과
<img width="427" height="173" alt="image" src="https://github.com/user-attachments/assets/bd2d3379-b1fc-4dfa-8f0d-7e48c8efd593" />

### 4) 부서별 월별 로그인 수
```
select substr(ri.create_date, 3, 2) as month, u.hr_organ as dep
from statistic.request_info ri, statistic.user u
where left(ri.create_date, 2) = 20 and ri.user_id = u.user_id and ri.request_code = 'L';
```
- 결과
<img width="536" height="242" alt="image" src="https://github.com/user-attachments/assets/dda09ead-48a8-46cc-ac85-27e62050ef4e" />
