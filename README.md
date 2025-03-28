# hanghae99_1st_week
항해99 1주차 과제


Q.
protected 는 어떤 상황에서 쓰는가? 이 역시 private 와 마찬가지로 test 가 어려울것 같은데 실무에서는 어떤형식으로 사용하는지 궁금하다.


# 에러 추적 기록
## 아고라 공유
테스트코드를 수행시키다가 java.lang.IllegalArgumentException 예외와 함께 500 에러를 만나게 되었습니다.
코드에 문제가 없는데 에러가 계속 발생하더라구요.
app 을 gradlew bootRun 으로 up 시켜서 해도 동일하게 500이 떨어졌었습니다.

결론적으로 -parameters 매개변수를 추가해주어야 하더군요.
intellij > 빌드, 실행, 배포 > 컴파일러 > Java 컴파일러 > 추가 명령줄 매개변수 : -parameters 추가

build.gradle.kts 파일 마지막줄에 다음 추가
tasks.withType<JavaCompile> {
options.compilerArgs.add("-parameters")
}

이러고 나니 모든 코드가 정상동작 했습니다.
컴파일러에서 api 파라미터를 정상적으로 인지하지 못하여 발생하는 오류라고 파악하고 있으나, 정확하진 않습니다.
혹시 아시는분 있으면 공유 부탁드립니다.


[에러 추적 상세 기록]
본 영역은 구구절절 에러를 어떻게 추적하였는지 기록하였습니다. 스킵하셔도 됩니다.

그래서 서비스영역을 아애 배제하고, controller 영역에서 print 를 찍게 하고 테스트를 동작시켰는데, print 문도 안찍혔습니다.
저는, parameter 를 자동으로 validation 하도록 라이브러리를 추가하고, 코드를 수정했는데, 이게 원인인가 싶어서 관련 코드도 싹 삭제하고, 완전히 기본만 있는, parameter 호출, service 영역없이 print 했는데도 print 찍는것 없이 500에러.
그러면 다른 endpoint 를 모두 삭제해보자! 아주 간단한 코드만 남겨두고 테스트. 또 500에러.

저는 GlobalExceptionHandler 를 작성했었습니다.
(코드를 자세히 읽어보셨으면 아셨겠지만, ApiControllerAdvice 에 ExceptionHandler 가 있어요..)
저는 ApiContollerAdvice 에 ExceptionHandler 가 있는줄 몰랐죠..
그래서 제 코드에서 계속 디버깅을 하고, 디버깅이 안잡히고.. 머리쥐어뜯고 반복이였습니다..ㅠㅠ (과제 코드 열심히 분석하고 시작합시다..ㅠ)
그러다 끝에 ApiContollerAdvice 의 ExceptionHandler 를 발견.. 원인을 파악하고, 위의 해결책을 찾아 해결하였습니다.

혹여나.. 제 뻘짓이 누군가에게 도움이 될까 싶어 글을 남깁니다.