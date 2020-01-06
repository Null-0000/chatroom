#1. 实现的功能:       
- ##注册
     用户名，密码，签名（可空），图片  
- ##登录：  
    用户通过账号及密码登录，不可重复登陆
- ##修改用户个人信息：
    允许用户修改自己的昵称，签名，头像。
- ##收发消息:   
    收发消息均需经过服务器的处理，每条消息指定发送者和接收者，若接收者此刻没有在线上，则将消息储存到数据库中，否则直接通过Socket发送
    给指定的接收者
- ##显示消息：
    显示消息通过javafx中的WebView组件以html的形式显示。显示格式如下:
    1. 文本信息:
        ```html
            <div align="">
            <p>head</p>
            <p>content</p>
            </div>
        ```
    2.  数学公式：
        ```html
                <div align="">
                <p>head</p>
                <p>content<math>formula</math>...</p>
                </div>
            ```
    3. 图片：
        ```html
                <div align="">
                <p>head</p>
                <img src="" ></img>
                </div>
            ```
    4. 音频：
        ```html
                <div align="">
                <p>head</p>
                <audio control="controls">
                   <source src="" type=""></source> 
                </audio>
                </div>
            ```
- 群聊
#2.项目结构:
   - [程序文件结构](file:///D:/IdeaProjects/chatroom/src/list.txt)
   - [数据库结构]()
#3.关键class的功能描述：
   - 客户端：
        - model 
            - User
            - Dialog
            - DialogManager
            - ConnThread
            - ...
        - view
            - StageManager
            - AddFriendView
            - ChatView
            - MainView
            - ...
        - controller
            - AddFriendViewController
            - MainViewController
            - ...
   - Kit：
        - Data
        - Info
            - UserInfo
            - GroupInfo
        - Message
        - UserCard
        - IODealer
        - ClassConverter
   - 服务器：
        - ServerThread
        - DataBaseManager
        - ServerLauncher
        
       
#3.基本实现原理：
   - 服务器负责处理用户的请求或传递用户的信息，而不会主动向用户发送数据
   - 所有操作都是由客户端向服务器发送请求得到结果反馈实现
   - 注册
        - 用户通过注册界面向服务器发送自己的头像，昵称，密码等信息，服务器接收消息，并在数据库中存储
   - 登录
        - 用户登录，若密码正确，服务器会将用户的昵称，好友列表，群列表等信息返回
        - 第二，客户端会读取本地的消息记录
        - 第三，客户端请求服务器获得所有离线消息
        - 最后，客户端与服务器建立长连接，用于接收所有的即时消息
   - 收发消息
        - 聊天界面通过WebView显示，支持用户发送数学公式，表情，音频及图片等
        - 程序获取用户所发送内容，统一转换成byte数组
   - 建群，加群，加好友
   - 将消息记录保存到本地
   
#4.project难点：
   - 图形界面有关的类与其他类关系较复杂，如好友列表的实时更新和储存，在本地储存过程中，需要将一些ListProperty等javafx有关的组件序列化
   - 客户端与服务器通信的协议，由于发送的内容更加复杂，发送协议和发送方式经过了多次较大的修改
   - 实现的功能较多，细节繁杂，导致代码量较大 大约有 3600+ 行 （不含fxml，css等其他文件）
   
