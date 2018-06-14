//
//  Ethereum.swift
//  OCPay
//
//  Created by 何自梁 on 2018/5/19.
//  Copyright © 2018年 menggen. All rights reserved.
//

import UIKit
import EthereumKit

@objcMembers class Ethereum: NSObject {
    @objc static public func mnemonicCreate() -> [String]{
        // It generates an array of random mnemonic words. Use it for back-ups.
        // You can specify which language to use for the sentence by second parameter.
        let mnemonic = Mnemonic.create(strength: .normal, language: .english)
        return mnemonic;
    }
    
    
    public static func createSeed(mnemonic: [String], withPassphrase passphrase: String) -> Data{
        return Mnemonic.createSeed(mnemonic: mnemonic, withPassphrase: "password")
    }
    
    
    public static func cteateWalletWithSeed(netWork:Network) -> String{
        let wallet = Wallet(network: .main, privateKey: "56fa1542efa79a278bf78ba1cf11ef20d961d511d344dc1d4d527bc06eeca667")
        return wallet.generateAddress()
    }
    
    
     public static func generateAddress(seed: Data ,netWork:Network) -> String?{
//        let wallet: Wallet
//        do {
//            wallet = try Wallet(seed: seed, network: .main)
//            let address = wallet.generateAddress()
//            return address
//        } catch let error {
//            // Handle error
//            print(error)
//            return nil
//        }
        
        guard let wallet = try? Wallet(seed: seed, network: .ropsten) else { return nil}
        return wallet.generateAddress()
    }

    public static func send(){
//        let rawTransaction = RawTransaction(ether: "0.15", to: address, gasPrice: Converter.toWei(GWei: 10), gasLimit: 21000, nonce: 0)
//        let tx = try wallet.signTransaction(rawTransaction)
//
//        geth.sendRawTransaction(rawTransaction: tx) { result in
//            // Do something...
//        }
        
    }
   
}
