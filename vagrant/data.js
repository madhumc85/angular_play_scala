db.dropDatabase()

db.forms.insert( { form: "N-400", desc: "Apply for Citizenship", fee: 595, url: "http://www.uscis.gov/n-400" } )

db.forms.insert( { form: "I-485", desc: "Apply for a Green Card", fee: 985, url: "http://www.uscis.gov/i-485" } )

db.forms.insert( { form: "I-130", desc: "Help My Relative Immigrate", fee: 420, url: "http://www.uscis.gov/i-130" } )

db.forms.insert( { form: "I-765", desc: "Apply for Employment Authorization", fee: 380, url: "http://www.uscis.gov/i-765" } )

db.forms.insert( { form: "I-9", desc: "Employment Verification", fee: 0, url: "http://www.uscis.gov/i-9" } )
