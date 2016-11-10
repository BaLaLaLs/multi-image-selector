/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */
import React, {Component} from 'react';
import {
    AppRegistry,
    StyleSheet,
    NativeModules,
    TouchableNativeFeedback,
    ScrollView,
    Text,
    Image,
    View
} from 'react-native';
import MultiImage from 'react-native-multi-image-selector'
export default class MyProject extends Component {
    constructor() {
        super();
        this.state = {
            imageArray: []
        }
    }

    render() {
        return (
            <TouchableNativeFeedback style={styles.container}
                 onPress={()=> {
                     MultiImage.pickImage({}).then((imageArray)=> {
                         this.setState({imageArray: imageArray})
                     }).catch(e=> {
                    });
             }}>
                <View style={styles.container}>
                    <TouchableNativeFeedback style={{margin: 5}} onPress={()=> {
                        this.setState({imageArray: []})
                    }}><Text>图片展示</Text></TouchableNativeFeedback>
                    <ScrollView style={{flex: 1}}>
                        {this.state.imageArray.map(data=> {
                            return (<Image source={{uri: data}} style={{width: 200, height: 200}}/>)
                        })}
                    </ScrollView>
                </View>
            </TouchableNativeFeedback>
        );
    }
}
const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#F5FCFF',
    },
    welcome: {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
    },
    instructions: {
        textAlign: 'center',
        color: '#333333',
        marginBottom: 5,
    },
});
AppRegistry.registerComponent('MyProject', () => MyProject);
