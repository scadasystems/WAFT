# ONLINE TEST -> https://han.gl/WAFT


Need for me -> https://scadasystems.github.io/
<br>[![Github](https://user-images.githubusercontent.com/38491289/55671914-e54cc900-58cf-11e9-8bd0-75fe11ce96ea.PNG)](https://github.com/scadasystems)
![show project](https://user-images.githubusercontent.com/38491289/55539064-c6490e00-56fa-11e9-8514-066251fd0093.PNG)

---

# 개요
**예전**과는 다르게 최근, 많은 사람들이 **국내 여행보다 해외 여행**을 더 많이 가고 싶어하며 출국하고 있습니다. 그러나 대부분의 사람들은 **다른 나라의 언어를 사용할 수 없기 때문에** 편한 여행 패키지를 이용하거나 **번역기**, 직접 인터넷으로 찾은 **출력물**들을 이용하며 여행합니다.
해외 여행은 가고 싶은데 그 나라의 언어 및 정보 등을 **한번에** 다루는 앱이 없었습니다. 이러한 **기능들이 한가지 앱**으로 들어있는 어플리케이션이 있다면 정말 좋겠다고 생각을 했습니다. 
실질적으로 해외 여행을 다니면서 불편했던 경험들이 많았기 때문에 그러한 것들을 **해결해 보고자 생각**하게 되었습니다. 그래서 **해외 여행에 도움이 되는 정보**(환전소, 여행지 안내)와 **여러가지 편의기능**(채팅-번역 서비스, SOS등)을 제공할 수 있는 앱을 개발하고자 합니다.
***

# 앱 이름의 이유
# "WAFT 란?" ![icon](https://user-images.githubusercontent.com/38491289/55669114-8e82c780-58ae-11e9-98ae-3cde5435df74.png)
> 'WAFT' 는 사전적 의미로 '(공중에서 부드럽게)' 퍼지다 라는 뜻도 있으며,
World Adviser's help for Friendly Travel 의 약어로써 
'친근한 여행을 위한 조언자의 도움' 이라는 뜻도 있습니다.

 해외여행에 필요한 여러가지 편의기능 및 툴을 생각하여 이러한 이름으로 정했습니다.
 ***
 
# 요구사항
###### 어플에 들어갈 사항들입니다. 

* **회원가입과 로그인 구현**을 위한 [Firebase](https://firebase.google.com/) 연동
* **언어 번역**을 위해 [Naver사의 Papago 인공신경망 번역 API](https://developers.naver.com/docs/papago/) 를 활용
* **지도 검색**을 위한 안드로이드 위치 권한 허용 - [Permission Setting Code List.txt](https://github.com/scadasystems/WAFT/files/2986161/Permission.Setting.Code.List.txt)
* **길 안내**를 위해 [구글 지도 API](https://enterprise.google.com/intl/ko/maps/)를 활용
* **SOS**를 위해 위치 권한을 사용하여 현재 본인이 위치해 있는 나라에 맞는 police 번호 및 대사관 번호를 스마트폰으로 제공 
(안드로이드 전화 권한 사용) 
* **채팅**을 위해 Firebase 연결
* 채팅에는 친구 추가, 상태 메세지, 닉네임, 프로필 사진이 있으며 친구 추가는 QR 코드로도 가능
* 채팅방 안에는 기존의 채팅앱들과 비슷한 구조로 되어 있으나 **실시간 위치 공유**를 통해
  친구들이 어디 있는지 확인할 수 있다. (ex> 놀러오라고 장소를 지정해주지만 못찾는 친구를 위함)
  `단, 보안을 위해 허락 시스템을 만들어 서로간의 허락이 없다면 위치 공유는 불가능하다.`
***

# 레이아웃 구성 - 스케치
![1](https://user-images.githubusercontent.com/38491289/54652641-db1e8280-4afa-11e9-992b-6f14247704a6.png) 
![3](https://user-images.githubusercontent.com/38491289/54652649-dfe33680-4afa-11e9-9d57-46434aed5038.png)
![2](https://user-images.githubusercontent.com/38491289/54652646-dd80dc80-4afa-11e9-9311-58ea0a04ce40.png) 
***

# 동작과정
* 로그인 및 회원가입(가입시 **개인 QR 코드** 자동생성)
* QR코드를 이용해 외국인과 원활한 **채팅 및 친구추가** 기능(채팅시 자동 번역 전송)
* 안내 : 구글 지도 API를 이용해 해외에서도 **가고싶은 지역을 안내**
* 환전소 : 해외에 **환전소를 안내**해주며 현재 환전율과 환전율 순위별로 나열
* 채팅방 : 다른 사람과 채팅한 내역 및 **실시간 채팅**을 보여줌
* SOS : **대사관과 현지 police**를 자동으로 연결시켜줌 (위험시에 활용)
`(팝업을 이용하여 ‘연결하기-취소하기” 이용. 잘못 누름 대비)`
* 서브메뉴 친구목록 : QR코드를 공유한 사람과 수락을 하면 **친구목록**을 리스트뷰 형식으로 보여줌
* 설정 : 공지 사항, 개인/보안, 고객 센터를 이용하여 **공지 사항**에는 앱 사용 시 유의 사항을 
안내, **개인/보안**에서는 공유하고 싶은 자신의 정보 수정, **고객 센터**를 활용하여 앱 이름과
문의 사항(어플 관리자) 번호 표시
***

# 기대효과 및 활용방안(시장성 및 기대 효과)
* 실제로 외국인과 편리하게 채팅을 할 수 있어 채팅앱으로도 무난함
* 각 나라의 정보(환전소, 여행지)를 알려줌으로 내비게이션 시스템으로도 가능
* SOS를 이용하여 위험 상황시 비상연락망으로 사용할 수 있어 비상시에도 사용	하기 무난함
* 현재 어플 중에는 번역 어플 밖에 없으므로, 언어를 못하거나 여행지역에 대해 잘 몰라도 사용하기 편리하여 시장성으로도 좋다고 본다.
* 친구 추가를 할 시 상대방에게 자신의 위치를 알려 줄 수 있어 서로가 쉽게 찾을 수 있다.
***
### Collaborators
| Name | Email | Personal Site |
| - | - | - |
| 양윤모| redsmurf@lulzm.org | https://github.com/scadasystems?tab=repositories
| 나광진 |  skrhkdwls77@gmail.com | https://github.com/NaKwangJin
| 윤장원 | yunjang2@gmail.com | https://github.com/jangdoll

### 담당분야
| 이름 | 담당분야 | 수행역할 |
| - | - | - 
| 양윤모 | Back-end 개발 | JAVA / Kotlin 개발, Chatting 구현, 공공 API 및 Firebase 이용하여 DB 구성 
| 윤장원 | Front-end 개발, 디자인 | 레이아웃 구성과 전반적인 디자인 구성
|나광진 | Front-end 개발, 기획자 | 전반적인 구성 기획 및 메인, 메뉴 레이아웃 구성과 디자인
***

# 기타
#### Firebase 를 이용
![firebase](https://user-images.githubusercontent.com/38491289/54652857-e0300180-4afb-11e9-9e31-a3d5c5f2f374.PNG)

#### QR 코드
Zxing 라이브러리 참조 - https://github.com/journeyapps/zxing-android-embedded
```groovy
repositories {
    jcenter()
}
dependencies {
    implementation 'com.journeyapps:zxing-android-embedded:3.6.0'
    implementation 'com.android.support:appcompat-v7:25.3.1'   // Minimum 23+ is required
}
android {
    buildToolsVersion '27.0.3' // Older versions may give compile errors
}
```
#### 채팅 관련
카카오톡 개발자들 질의응답 사이트 - https://devtalk.kakao.com/

#### 현재 쓰인 퍼미션(권한)
```groovy
<uses-permission android:name="android.permission.GET_ACCOUNTS"/>
<uses-permission android:name="android.permission.READ_PROFILE"/>
<uses-permission android:name="android.permission.READ_CONTACTS"/>
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.WRITE_SYNC_SETTINGS"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

#### 현재 쓰인 라이브러리
``` groovy
// firebase
implementation 'com.google.firebase:firebase-auth:16.2.0'
implementation 'com.google.firebase:firebase-core:16.0.8'
implementation 'com.google.firebase:firebase-database:16.1.0'
implementation 'com.google.firebase:firebase-messaging:17.5.0'
implementation 'com.google.firebase:firebase-storage:16.1.0'
// FirebaseUI for Firebase Realtime Database
implementation 'com.firebaseui:firebase-ui-database:3.3.1'
// Material design
implementation 'com.google.android.material:material:1.1.0-alpha05'
// 리사이클러뷰, 카드뷰
implementation 'androidx.recyclerview:recyclerview:1.1.0-alpha04'
implementation 'androidx.cardview:cardview:1.0.0'
// Country-picker
implementation 'com.hbb20:ccp:2.2.6'
// PercentLayout
implementation 'androidx.percentlayout:percentlayout:1.0.0'
// circle image view library
implementation 'de.hdodenhof:circleimageview:3.0.0'
// for stylist toast
implementation 'com.github.TheHasnatBD:SweetToast:1.0.1'
// lottie
implementation 'com.airbnb.android:lottie:2.8.0'
```
