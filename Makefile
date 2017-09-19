clean:
	rm -rf testapp

test:
	cordova create testapp com.test testapp
	cd testapp && cordova plugins add ..
	cd testapp && cordova platforms add android
	cd testapp && cordova build --browserify

run:
	cd testapp && cordova run --browserify
