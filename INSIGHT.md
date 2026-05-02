~~~
# 인증 흐름

Request
-> Security Filter Chain
    -> JwtAuthFilter
    -> ExceptionTranslationFilter
    -> authenticationEntryPoint
-> DispatcherServlet
-> Controller
-> @RestControllerAdvice

~~~

~~~
# 주기적으로 로그인이 해제됨

- token 만료기간은 AT15분 RT14일
- AT 만료 후 /refresh 요청
- refresh token RTR 롤링에서 오류 발견
- 신규 token 생성하고 DB에 insert할때
- hash token은 이전 hash로 넣고있었음

~~~ 

~~~
# JwtAuthFilter.shouldNotFilter()에서 /auth/login skip 로그가 두번씩 찍히는 이슈

추적:
- shouldNotFilter() /auth/login skip 로그는 두번 찍히는데, 
- doFilterInternal() /auth/me 는 한번 찍히는게 수상하여
- shouldNotFilter(arg) Servlet Request의 Hashcode를 확인함 -> 동일함
  -> 두번 연속으로 요청하는 로직이나 행위 없음
  -> 한번의 요청에서 필터링 프로세스의 문제라고 판단함

원인:
1. Spring Boot가 JwtAuthFilter Bean을 일반 Servlet Filter로 자동 등록

2. addFilterBefore 로 등록된 JwtAuthFilter (중복 등록)
   -> 중복으로 필터 등록됨. 
   
3. shouldNotFilter()
   2번 호출됨 (중복 등록 상황이라 정상)

4. doFilterInternal()
   shouldNotFilter()가 false인 요청은 첫 번째 필터에서 
   already-filtered request attribute가 설정된다.
   -> 그 상태로 두 번째로 등록된 같은 JwtAuthFilter를 만나면 
      OncePerRequestFilter가 doFilterInternal() 재진입을 막는다.
   -> 1회 호출됨

5. /auth/login API는 검증 whiteList로 
   shouldNotFilter()를 거쳐 토큰 검증을 건너뛰도록 설계함
   -> 2번 호출됨

6. /auth/me API는 검증대상
   skip 하지않고 첫 필터에서 doFilterInternal() in.
   -> 이후 필터는 무시 처리됨
   -> 1번 호출됨

해결:
1. Bean은 유지
2. Spring Boot가 JwtAuthFilter 자동등록하지 못하도록 처리함 
   -> registration.setEnabled(false)

실제로는 skip 처리되기 때문에 검증필터는 1번만 거치고,
기능에는 이상이 없었지만 DEBUG시 혼란이 있었음
~~~