import Recognizer from "./Recognizer.js";
// import Speaker from "./Speaker.js";

export default class SpeakerTag extends HTMLElement{
  constructor() {

    super();

    this.attachShadow({mode: "open"}); 
    this.shadowRoot.innerHTML = `
        <style>
          .canv {
            height: 100px;
            width: 100px;
            display: none;
          }

          #container {
            background-color: #333333;
            height: 100px;
            width: 320px;
            display: block;
            margin-top: 20px
          }

          #icon-div {
          }

          #slider-div {
            flex-grow: 2;
            flex-basis: 30%;
            width: 300px;
            height: 30px;
            margin-top: 40px;
          }

          select{
            -webkit-appearance: none;
            -moz-appearance: none;
            appearance: none;
            margin: 0;
            height: 30px;
            width: 100px;
            background: transparent;
            position: relative;
            z-index: 1;
            padding: 0 40px 0 10px;
            border: 1px solid white;
            color: white;
            outline: none;
          }

          select::-ms-expand {
              display: none;
          }

          select:hover{
          border-color: #FF5959 ;
            
          }

          option{
            background-color: #333333;
          }

          #selectW{
              display: inline-block;
              height:30px;
              position:relative;
          }

          #selectW::before{
            content: '';
            position: absolute;
            z-index: 0;
            top: 0;
            right: 0;
            background: #ccc;
            height: 100%;
            width: 30px;
          }

          #selectW::after{
            content: '';
            position: absolute;
            z-index: 0;
            top: 0;
            bottom: 0;
            margin: auto 0;
            right: 9px;
            width: 0;
            height: 0;
            border-style: solid;
            border-width: 6px 6px 0 6px;
            border-color: #FF5959 transparent transparent transparent;
          }
          
          #record-btn {
            background-color: white;
            border: none;
            color: #FF5959;
            height: 30px;
            width:130px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 20px;

            -webkit-transition-duration: 0.4s; /* Safari */
            transition-duration: 0.4s;
            cursor: pointer;
            border-radius:2px;
            font-family: Bahnschrift;
            position: relative;

          }

          input[type=range]{
            margin-top: 8px;
            outline: none;
            -webkit-appearance: none;    
            background: -webkit-linear-gradient(#FF5959, #FF5959) no-repeat, #ddd;
            height: 3px;
            position: relative;
            top: -65px;
            left:50px;
            
          }

          input[type=range]::-webkit-slider-thumb {
              -webkit-appearance: none;
              height:16px;
              width: 16px;
              background: #fff;
              border-radius: 50%;
              border: solid 1px #ddd;
          }
          .rate_p{
          color: white;
          width:275px;
      
          padding-left: 45px;
          border:1px solid #757575;
          border-radius:2px;
          position: relative;
          top: -20px;

          text-align:left;
          }
          .pitch_p{
          color: white;
          width:275px;
          
          padding-left: 45px;
          border:1px solid #757575;
          border-radius:2px;
          position: relative;
          top:-60px;

          text-align:left;
          }
          #pitch{
          position: relative;
           top:-105px;
          }


        </style>
        <div id="container" class="speaker-tag-container">
          <div class="row" style="display:flex;width:320px">
            <div id="icon-div" style="width:120px">
              <canvas class="canv" id="recording"></canvas>

              <input type="button" id="record-btn" value="record" style="left:15px"></input>
            </div>
            <div id="selectW" style="left:60px">
              <select id="voice" style="width:9em">
                <optgroup label="男性" style="color:black">
                  <option value="koutarou" style="background-color:gray">男性高め</option>
                  <option value="osamu" style="background-color:gray">男性普通1</option>
                  <option value="seiji" style="background-color:gray">男性普通2</option>
                  <option value="hiroshi" style="background-color:gray">男性低め</option>
                </optgroup>
                <optgroup label="女性" style="color:black">
                   <option value="kaho" style="background-color:gray">女性高め</option>
                   <option value="nozomi" style="background-color:gray">女性普通1</option>
                   <option value="akari" style="background-color:gray">女性普通2</option>
                   <option value="nanako" style="background-color:gray">女性低め</option>
                </optgroup>
                <optgroup label="キャラクター" style="color:black">
                  <option value="sumire" style="background-color:gray">結月 ゆかり</option>
                  <option value="maki" style="background-color:gray">弦巻 マキ</option>
                  <option value="anzu" style="background-color:gray">月詠 アイ</option>
                </optgroup>
              </select>
            </div>
          </div>
          <div id="slider-div">
            <p class="rate_p">速さ: </p><input type="range" id="rate"  min='0.0' max='2.0', step='0.1' style="width:180px;left:70px">

            <p class="pitch_p">高さ: </p><input type="range" id="pitch" min='0.0' max='2.0', step='0.1' style="width:180px;left:70px">
             </div>
            
        </div>
      `;

    this.recognizer = new Recognizer();
    this.callback = null;

    this.canvas = this.shadowRoot.getElementById("recording");
    this.context = this.canvas.getContext("2d");
    this.canvas.width = 40;
    this.canvas.height = 40;

    this.show_stop_recording();
    // this.set_keydown();
    this.recognizer_active = false;

    this.voice_select = this.shadowRoot.getElementById("voice");

    this.record_btn = this.shadowRoot.getElementById("record-btn");


    this.rate_range = this.shadowRoot.getElementById("rate");
    this.pitch_range = this.shadowRoot.getElementById("pitch");


    this.record_btn.onclick = () => {
        if (this.recognizer_active){
            if (!this.recognizer.is_recognizing) {
                this.recognizer.start();
                this.show_recording();
                this.recognizer.flag_speech = true;
                this.record_btn.value = "stop";
                this.record_btn.style.backgroundColor='#FF5959';
                this.record_btn.style.color='white';
            } else {
                this.recognizer.stop();
                this.show_stop_recording();
                this.recognizer.flag_speech = false;
                this.record_btn.value = "record";
                this.record_btn.style.backgroundColor='white';
                this.record_btn.style.color='#FF5959';
            }
        }

    }


  }

  set recognizer_active (b) {
    this._recognizer_active = b;
    if (!b) {
      this.recognizer.stop();
      this.show_stop_recording();
      this.recognizer.flag_speech = false;
    }
  }

  get recognizer_active () {
    return this._recognizer_active;
  }

  set callback (f) {
    this._callback = f;
    this.recognizer.callback = (text) => {
        this.text = text;
        console.log(this);
      let v = this.voice_select.value;
      let p = this.pitch_range.value;
      let r = this.rate_range.value;
      console.log("callback", this.text, v, p, r);
    };
  }

  get callback () {
    return this._callback;
  }

  set text(text) {
      //TODO set voice pitch, rate to callback
      this.callback(text, this.voice_select.value, parseFloat(this.pitch_range.value), parseFloat(this.rate_range.value));
  }

  get text() {
    return "";
  }

  /*
  set_keydown() {
    document.addEventListener('keydown', (event) => {
        const keyName = event.key;
        if (keyName === " ") {
          if (this.recognizer_active && !this.recognizer.is_recognizing) {
            this.recognizer.start();
            this.show_recording();
            this.recognizer.flag_speech = true;
          }
        }
        else if (keyName == "q") {
          this.recognizer.stop();
          this.show_stop_recording();
          this.recognizer.flag_speech = false;
        }
      }, true);
  }
  */

  double_ring(color) {
    this.context.fillStyle = color;
    this.context.beginPath();
    this.context.arc(20, 20, 20, 0, 2 * Math.PI, false);
    this.context.fill();
    this.context.fillStyle = 'rgb(255, 255, 255)';
    this.context.beginPath();
    this.context.arc(20, 20, 18, 0, 2 * Math.PI, false);
    this.context.fill();
    this.context.fillStyle = color;
    this.context.beginPath();
    this.context.arc(20, 20, 15, 0, 2 * Math.PI, false);
    this.context.fill();
  }

  show_recording() {
    this.double_ring('rgb(255, 0, 0)');
  }

  show_stop_recording() {
    this.double_ring('rgb(0, 0, 0)');
  }
}

customElements.define("speaker-tag", SpeakerTag);

