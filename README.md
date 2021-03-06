# ProjectM
![14] <br>
피하지 못하면 풀어라 - 피풀

### 플랫폼

- android (kotlin)
- iOS (추후 개발 예정)

### 프로젝트 설명

시험을 준비할 때, 가끔씩 (혹은 자주) 켜게되는 휴대폰. 만약 잠금화면에서 퀴즈가 나오고, 틀리면 휴대폰을 사용할 수 없다면? 
잠금화면에 퀴즈를 띄워주고, 틀리면 응징한다. 
물론 어플리케이션 자체에서 문제를 풀고, 오답 체크한 것들을 모아서 복습하는 기능까지 제공한다.

### 개발 기간

2018.09 ~ 2018.11

### 담당 업무

총 3명의 팀 (영업, 디자인, 개발) 중 개발 담당

### 프로젝트에 사용된 기술

- kotlin
- sqlite
- android service 에 layout 띄우기

### 어플리케이션 흐름과 설명

#### 어플리케이션

![12] <br>
어플리케이션 스플래시 화면.

![2] <br>
로그인이 안되어 있으면 로그인 화면이 나온다. 하단에 계정 만들기 버튼을 눌러 계정을 만들어 로그인 할 수 있다.

![1] <br>
로그인 하면 보이는 메뉴화면.
 OX 퀴즈 : 문제 카테고리 리스트 화면으로 이동한다.
 오답 노트 : 오답 문제 카테고리 리스트 화면으로 이동한다.
 설정 : 설정 화면으로 이동한다.

![7] <br>
다양한 카테고리들이 존재하고, 카테고리 버튼은 category db에 담긴 내용으로 동적으로 생성된다.

![8] ![17] <br> 
문제를 띄워주고 다음, 이전문제로 이동 할 수 있는 화면이다.
우측 상단의 노란 별을 누르면 오답노트에 체크되고, 오답 노트에 체크된 문제는 빨간 별이 표시가 된다.

![20] ![16] <br>
하단의 원을 드래그 해서 O / X 로 이동 시키는 것이 가능하며, 문제를 맞출 경우, 틀릴 경우 다른 피드백을 준다. (틀릴때 애니메이션을 주어 문제가 흔들린다.)

![15] <br>
메뉴에서 오답노트 버튼을 누르면 나오는 카테고리 리스트, 오답 문제들 전부를 모아보는 버튼을 별도로 구성하였다.

![9] <br>
 잠금 슬라이드 설정 : 잠금화면 on off / 강제 잠금 시간을 설정하는 화면
 스킨 설정 : 8가지 스킨중에 한가지를 고르는 화면
 폰트 설정 : 어플리케이션 메인 폰트를 설정하는 화면
 로그아웃 : user db 연결을 끊고 로그인 화면으로 이동한다.

![13] ![3] ![4] <br>
번거로울수 있는 강제잠금화면을 on / off 할 수 있는 기능과
강제잠금화면에서 문제를 틀릴때 패널티를 설정할 수 있는 화면

![10] ![11] <br>
8가지 다양한 스킨을 지원하며 버튼 한번 누르면 설정이 저장된다. 우측 이미지는 Dark Grey 스킨을 적용한것.

![22] ![21] <br>
여러 무료 폰트중에 하나를 골라서 변경할 수 있다. 우측은 변경한 이미지

![5] <br>
로그아웃하면 로그인 화면으로 이동한다.

<hr>

#### 잠금화면

![18] ![19] <br>
잠금 화면은 문제 하나를 보여주고 정답을 맞춰야 사라진다. 잠금화면을 최상위 뷰로 세팅하여 잠금화면이 사라지기 전까지 휴대폰을 가지고 다른 활동은 할 수 가 없다.
오답시 강제잠금시간 설정한 만큼 대기하며 문제를 복습하게 된다.
상단의 노란버튼을 누르면 해당 문제를 오답노트에 기록할 수 있다.

#### 기타 사항

개발 하며 의문이였던 사항이나 문제사항들 몇몇 가지 블로그에 정리 하였고, 좀더 가다듬어서 올릴 예정이다. : http://greedy0110.tistory.com/

[1]: images/greedy0110로그인성공.jpg
[2]: images/greedy0110로그인실패.jpg
[3]: images/강제잠금설정화면.jpg
[4]: images/강제잠금시간선택.jpg
[5]: images/로그아웃누르면.jpg
[6]: images/메뉴화면.jpg
[7]: images/문제카테고리화면.jpg
[8]: images/문제화면.jpg
[9]: images/설정화면.jpg
[10]: images/스킨설정화면.jpg
[11]: images/스킨을바꾸면.jpg
[12]: images/스플래쉬화면.jpg
[13]: images/슬라이드켜기끄기화면.jpg
[14]: images/아이콘.png
[15]: images/오답노트가없는항목을눌렀을때.jpg
[16]: images/오답시화면.jpg
[17]: images/우측상단노란별누르기.jpg
[18]: images/잠금화면.jpg
[19]: images/잠금화면에서문제에틀리면.jpg
[20]: images/정답시화면.jpg
[21]: images/폰트변경.jpg
[22]: images/폰트선택.jpg

### 다운로드 경로