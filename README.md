rxandroid-usecase
=================

RxJavaとかRxAndroidよくわからないので、よくある要求に対してRxAndroidを使って実装するとどうなるかを試すリポジトリです。RxJavaの使い方として正しいコードが書かれているとは限らないのでご注意ください。

I don't understand RxJava or RxAndroid yet. I'll implement typical case of Android, using RxAndroid. (+o+)


## environment

Will change rapidly...

- Groovy 2.4.0-beta-3
- rxandroid 0.23.0
- multidex 1.0.0
- swissknife 1.1.4
- greenbot/EventBus 2.4.0
- retrofit 1.8.0


## Form Validation


### require

emailとpasswordを入力するフォームがある。submitボタンを押した時、それぞれのフォームの空チェックを行い、エラーの場合送信を中止し、エラー表示を行う。

![](./art/form_validation.png)

### implement

まずは空チェックをして結果を返すObservableを生成するクラスを作る。
`notEmpty()`はsubscribeされた時に、指定したTextViewが`empty`だとfalse, `!empty`だとtrueを送出するObservable。 success, failureを引数に受け取っているが、これは個別のバリデーション結果に対してアクションを起こしたい場合に指定する。

```groovy
@CompileStatic
class FormValidator {

  def static Observable<Boolean> notEmpty(TextView textView,
      Action0 success,
      Action0 failure) {
    return rx.Observable.create({ Subscriber<? super Boolean> subscriber ->
      if (TextUtils.isEmpty(textView.getText())) {
        if (failure != null) {
          failure.call()
        }
        subscriber.onNext(false)
      } else {
        if (success != null) {
          success.call()
        }
        subscriber.onNext(true)
      }
      subscriber.onCompleted()
    } as Observable.OnSubscribe<Boolean>)
  }
}
```

`FormValidator`を使ってvalidation対象のEditTextからObservableを作る。それをconcatでつなげる。concatして作ったObservable<Boolean>に`reduce()`と`filter()`をかける。`reduce()`はconcatでつなげたObservable<Boolean>の結果を&して、全てのvalidationが通ったかを表すBooleanを送出する。`filter()`は`reduce()`の結果をそのまま返す。この時falseだったら後続の処理が呼ばれない。
RxAndroidの`ViewObservable.clicks()`を使ってクリックイベントをもらい、その中でをsubscribeする。subscribeする時に渡すAction1が呼び出される時は全てのvalidtionがtrueで通過している状態になる。

```groovy
@InjectView(R.id.edit_email)
EditText email;

@InjectView(R.id.text_email_error)
TextView emailError;

@InjectView(R.id.edit_password)
EditText password;

@InjectView(R.id.text_password_error)
TextView passwordError;

@InjectView(R.id.buttom_submit)
Button submit;

@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_form_validation);

  def validators = prepareValidation()
                .reduce({ a, b -> a && b })
                .filter({ a -> a })

  ViewObservable
      .clicks(submit)
      .subscribe(
      { event ->
        //クリックイベントがくる度に
		//validatorsをsubscribeする
        validators.subscribe({ _ ->
          submit()
        })
      })
}

def Observable<Boolean> prepareValidation() {

  //emailをvalidationするObservableを作る
  Observable<Boolean> emailEmptyValidator = FormValidator
      .notEmpty(email,
      hideError(emailError),
      showError(emailError, "*Enter your e-mail address."))

  //passwordをvalidationするObservableを作る
  Observable<Boolean> passwordEmptyValidator = FormValidator
      .notEmpty(password,
      hideError(passwordError),
      showError(passwordError, "*Enter your password."))

  //concatで２つのObservableをつなげて返す
  return Observable.concat(emailEmptyValidator, passwordEmptyValidator)
}
```

補助的なメソッド達。
Support methods.


```groovy
def static Action0 hideError(TextView errorView) {
  return {
    errorView.setVisibility(View.GONE)
  } as Action0
}

def static Action0 showError(TextView errorView, String message) {
  return {
    errorView.setText(message)
    errorView.setVisibility(View.VISIBLE)
  } as Action0
}
```

バリデーションの種類はFormValidatorにメソッドを追加していけばよい。それらをconcatで繋げればいくらでもバリデーションの処理を追加できる。FormValidatorは画面の実装とは独立しているので使いまわせる。

## Form validation 2

### require

emailとpasswordを入力するフォームがある。emailとpasswordがinvalidな間、submitをdisabledにする。
validationは以下の通り

__email__

- 空ではない

__password__

- 空ではない

### implement

FormValidatorに`watchNotEmpty()`を追加する。`ViewObservable.text()`を使って指定したTextView(EditText)の内容の変化を監視する。`map`してOnTextChangeEventのTextViewの中身を空チェックするObservable<Boolean>を返却する。

```groovy
class FormValidator {
  //...

  def static Observable<Boolean> watchNotEmpty(TextView textView, boolean emitInitialValue) {
    return ViewObservable.text(textView, emitInitialValue).
        map({ OnTextChangeEvent event ->
          return !TextUtils.isEmpty(event.text)
        })
  }
}
```

`FormValidator.watchNotEmpty()`を使ってバリデーションを実行するObservable<Boolean>を作成する。`Observable.combineLatest()`を使って２つのObservable<Boolean>を合成する。合成した結果を使って`submit`(Button)のenable/disableをセットする。

```groovy
@InjectView(R.id.edit_email)
EditText email;

@InjectView(R.id.edit_password)
EditText password;

@InjectView(R.id.buttom_submit)
Button submit;

@Override
protected void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_form_validation2);

  def emailEmptyObservable = FormValidator.watchNotEmpty(email, true);
  def passwordEmptyObservable = FormValidator.watchNotEmpty(password, true);

  Observable.combineLatest(emailEmptyObservable, passwordEmptyObservable,
      { a, b ->
        return a && b
      } as Func2)
      .subscribe(
      { Boolean isValid ->
        submit.setEnabled(isValid)
      })

  ViewObservable
      .clicks(submit)
      .subscribe(
      { _ ->
        submit()
      })
}
```

`Observable.concat()`ではなく`Observable.combineLatest()`を使っている。concatは渡したObservableがonComplete()を呼び出さないと次のObservableの結果を送出し始めない。combineLatestは指定したObservableの両方の値がonNext()で出揃ったらFunc2が実行される。


## Simple Network Access

TODO

## Rss Parse

TODO


## ListView paging

TODO

## View Binding

TODO

## RxAndroid in Fragment

TODO



## LICENSE

```
Copyright 2014 Toshihiro Yagi.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
